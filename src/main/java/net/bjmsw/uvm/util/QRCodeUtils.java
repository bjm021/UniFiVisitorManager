package net.bjmsw.uvm.util;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.bjmsw.uvm.model.Visitor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRCodeUtils {

    /**
     * Extracts the raw text payload from a downloaded UniFi QR Code PNG image.
     *
     * @param qrImageFile The PNG file downloaded from the UniFi API
     * @return The raw string payload encoded in the QR code, or null if unreadable
     */
    public static String decodeUniFiQR(File qrImageFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(qrImageFile);
            if (bufferedImage == null) {
                System.err.println("[QRCodeUtils] Could not read image file: " + qrImageFile.getName());
                return null;
            }

            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.err.println("[QRCodeUtils] No QR code found in the image.");
        } catch (IOException e) {
            System.err.println("[QRCodeUtils] Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[QRCodeUtils] Failed to decode QR: " + e.getMessage());
        }
        return null;
    }

    public static String downloadAndDecodeUniFiQR(ApiClient apiClient, Visitor visitor) {
        try {
            System.out.println("[UniFi QRCodeUtils] Downloading QR for " + visitor.getId());
            visitor.downloadQR(apiClient);
            System.out.println("[UniFi QRCodeUtils] QR downloaded successfully.");
            return decodeUniFiQR(new File("qr-data", visitor.getId() + ".png"));
        } catch (Exception e) {
            System.err.println("[QRCodeUtils] Failed to download QR: " + e.getMessage());
            return null;
        }
    }
}