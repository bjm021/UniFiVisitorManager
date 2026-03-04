<!DOCTYPE html>
<html>
<body style="font-family: sans-serif; background-color: #f3f4f6; padding: 20px;">

<div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">

    <h2 style="color: #1e3a8a; margin-bottom: 10px;">Welcome, ${visitorName}!</h2>
    <p style="color: #4b5563; font-size: 16px; line-height: 1.5;">
        You have been granted access for the upcoming event: <strong>${eventName}</strong>.
    </p>

    <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; padding: 15px; border-radius: 6px; margin-top: 25px; text-align: center;">
        <p style="color: #334155; font-weight: bold; margin-top: 0;">Add to your phone for quick access</p>
        <p style="color: #64748b; font-size: 14px; margin-bottom: 0;">
            If you are on an iPhone or Android, tap the <strong>AccessPass.pkpass</strong> attachment at the bottom of this email to add it to your Wallet.
        </p>
    </div>

    <div style="text-align: center; margin-top: 30px;">
        <p style="color: #64748b; font-size: 14px;">Or scan this QR code at the intercom:</p>

        <img src="cid:qr_image" alt="Access QR Code" style="width: 250px; height: 250px; border: 1px solid #e5e7eb; border-radius: 8px; padding: 10px;" />
    </div>

    <p style="color: #9ca3af; font-size: 12px; text-align: center; margin-top: 40px;">
        This access is temporary and will automatically expire. If you need assistance, please use the directory on the intercom.
    </p>

</div>

</body>
</html>