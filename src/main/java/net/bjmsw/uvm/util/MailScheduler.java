package net.bjmsw.uvm.util;

import de.brendamour.jpasskit.PKPass;
import jakarta.annotation.Nullable;
import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.Visitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Utility class responsible for scheduling and managing the execution of mail-related tasks.
 * It uses a worker thread pool to execute tasks asynchronously while ensuring they adhere
 * to a specified safety delay between mail actions.
 * <p>
 * This class allows for task scheduling and ensures efficient processing of asynchronous
 * mail actions through a thread-safe queue mechanism. Each task represents an individual
 * email-related operation, such as sending a visitor invitation.
 * <p>
 * Key Features:
 * - Thread-safe mail task queue for queuing tasks.
 * - Worker threads to process tasks in parallel.
 * - Configurable safety delay to prevent rapid consecutive mail sending.
 * - Support for custom mail tasks with the flexibility to include personalized content.
 */
public class MailScheduler {

    private final BlockingQueue<Runnable> mailBuilderQueue;
    private final BlockingQueue<Runnable> mailSenderQueue;
    private final Thread[] workers;
    private final Thread senderWorker;
    private final int smtp_safety_delay = 1000;
    private volatile boolean isRunning = true;

    public MailScheduler(int threadCount) {
        this.mailBuilderQueue = new LinkedBlockingQueue<>(1000);
        this.mailSenderQueue = new LinkedBlockingQueue<>(1000);

        this.senderWorker = new SenderWorker("SenderWorker");
        this.senderWorker.start();

        this.workers = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            workers[i] = new BuilderWorker("MailWorker-" + (i + 1), i);
            workers[i].start();
        }
    }

    public void scheduleMail(Visitor v, String eventName, String customMessage) {
        boolean queued = mailBuilderQueue.offer(new MailBuildTask(v, eventName, customMessage));
        if (!queued) {
            System.err.println("[MailScheduler] WARNING: Queue is full! Mail dropped for " + v.getEmail());
        } else {
            System.out.println("[MailScheduler] Mail task added to queue: " + v.getEmail());
        }
    }

    public void shutdown() {
        isRunning = false;
        for (Thread worker : workers) {
            worker.interrupt();
        }
        senderWorker.interrupt();
    }

    private class BuilderWorker extends Thread {
        private final int index;
        public BuilderWorker(String name, int index) {
            System.out.println("[MailScheduler] Starting worker: " + name);
            this.index = index;
            super(name);
        }

        public void run() {
            while (isRunning && !Thread.currentThread().isInterrupted())
                try {
                    Runnable task = mailBuilderQueue.take();
                    System.out.println("[MailBuilderWorker #" + index+1 + "] Worker executing task: " + task.getClass().getSimpleName());
                    task.run();
                } catch (InterruptedException e) {
                    System.out.println("[MailBuilderWorker #" + index+1 + "] Worker interrupted: " + Thread.currentThread().getName());
                    break;
                } catch (Exception e) {
                    System.err.println("[MailBuilderWorker #" + index+1 + "] Error executing mail task: " + e.getMessage());
                    e.printStackTrace();
                }
        }
    }

    private class SenderWorker extends Thread {
        public SenderWorker(String name) {
            System.out.println("[MailScheduler] Starting sender worker: " + name);
            super(name);
        }

        public void run() {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                try {
                    Runnable task = mailSenderQueue.take();
                    System.out.println("[MailSenderWorker] Worker executing task: " + task.getClass().getSimpleName());
                    task.run();
                } catch (InterruptedException e) {
                    System.out.println("[MailSenderWorker] Worker interrupted: " + Thread.currentThread().getName());
                    break;
                } catch (Exception e) {
                    System.err.println("[MailSenderWorker] Error executing mail task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    private class MailBuildTask implements Runnable {

        private final Visitor v;

        /**
         * Represents an optional custom message that can be provided during the creation
         * of a mail task. This message is used as part of the email content when sending
         * a visitor invite, allowing additional personalized information to be included
         * in the email. If not specified, the email content will exclude this optional
         * message.
         */
        @Nullable
        private final String eventName, customMessage;

        /**
         * Constructs a MailTask instance for sending an email invite to a specific visitor.
         *
         * @param v             the visitor for whom the mail task is created; contains the visitor's details
         *                      such as their name, email, and visit times
         * @param customMessage an optional custom message to be included in the email content;
         *                      may be null if no custom message needs to be sent
         */
        public MailBuildTask(Visitor v, String eventName, @Nullable String customMessage) {
            this.eventName = eventName;
            this.v = v;
            this.customMessage = customMessage;
        }

        @Override
        public void run() {
            try {
                Map<String, Object> model = new HashMap<>();
                model.put("visitorName", v.getFirstName());
                model.put("eventName", eventName);

                if (VisitorManager.getSettings().get("appleOrgName") != null) {
                    model.put("companyName", VisitorManager.getSettings().get("appleOrgName"));
                } else {
                    model.put("companyName", "Powered by UniFi Visitor Manager");
                }

                model.put("currentYear", TimeUtils.getCurrentYear());
                model.put("visitStartTime", TimeUtils.fromEpochSecondsToDateTimeString(v.getStart_time(), "E dd MMM yyyy HH:mm"));
                model.put("visitEndTime", TimeUtils.fromEpochSecondsToDateTimeString(v.getEnd_time(), "E dd MMM yyyy HH:mm"));
                if (customMessage != null && !customMessage.isEmpty()) {
                    model.put("customMessage", customMessage);
                }

                String htmlBody = TemplateUtil.render("email/visitor_invite.ftl", model);

                v.assignQR(VisitorManager.getApiClient());

                String qrData = QRCodeUtils.downloadAndDecodeUniFiQR(VisitorManager.getApiClient(), v);
                System.out.println("[UniFi QR] QR Code Data: " + qrData);


                PKPass pk = PKPassUtil.buildWalletPass(v, qrData, eventName, customMessage);
                byte[] pkdata = PKPassUtil.signAndZipPass(pk);

                if (mailSenderQueue.offer(new MailSendTask(v, eventName, htmlBody, pkdata))) {
                    System.out.println("[MailScheduler] Mail task added to queue: " + v.getEmail());
                } else {
                    System.err.println("[MailScheduler] WARNING: Queue is full! Mail dropped for " + v.getEmail());
                }
            } catch (Exception e) {
                System.err.println("[Template Error] Failed to render invite email template: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private class MailSendTask implements Runnable {
        private Visitor v;
        private final String htmlBody, eventName;
        private final byte[] pkdata;

        public MailSendTask(Visitor v, String eventName, String htmlBody, byte[] pkdata) {
            this.v = v;
            this.eventName = eventName;
            this.htmlBody = htmlBody;
            this.pkdata = pkdata;
        }

        @Override
        public void run() {
            try {
                File qrImage = new File("qr-data", v.getId() + ".png");

                MailUtil.sendVisitorInvite(
                        v.getEmail(),
                        v.getFirstName(),
                        v.getLastName(),
                        eventName,
                        pkdata,
                        qrImage,
                        htmlBody
                );

                System.out.println("[Email Success] Invite sent to " + v.getEmail());
                System.out.println("[UniFi QR] Deleting QR image: " + qrImage.getAbsolutePath() + " - Exists: " + qrImage.exists());
                qrImage.delete();
                System.out.println("[UniFi QR] Deleted QR image: " + qrImage.getAbsolutePath() + " - Exists: " + qrImage.exists());

                // safety delay after sending email to prevent rapid consecutive sends

                Thread.sleep(smtp_safety_delay);
            } catch (InterruptedException e) {
                System.out.println("[MailScheduler] Mail task interrupted: " + Thread.currentThread().getName());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("[MailScheduler] Failed to send email: " + e.getMessage());
            }
        }
    }

}
