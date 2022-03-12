package com.kuvyrkom.chartographer.usecase.service;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaServiceImpl;
import com.kuvyrkom.chartographer.domain.model.Charta;
import org.apache.commons.imaging.Imaging;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.UUID;

@Service
public class ChartographerService {

    private ChartaServiceImpl chartaService;

    public ChartographerService(ChartaServiceImpl chartaService) {
        this.chartaService = chartaService;
    }

    public String createNewCharta(int width, int height) throws IOException {
        String fileUUID = UUID.randomUUID().toString();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        String filePath = "src/main/resources/graph/" + fileUUID + ".jpg";
        compareAndWriteToFile(bufferedImage, filePath);

        Charta charta = new Charta(width, height, fileUUID, filePath);
        chartaService.save(charta);

        bufferedImage.flush();
        bufferedImage = null;
        System.gc();
        return fileUUID;
    }

    public void saveRestoredFragmentCharta(String id, int x, int y, int width, int height, MultipartFile multipartFile) throws Exception {
        File chartaFile = new File(chartaService.findByFileUUID(id).getFilePath());

        BufferedImage chartaImage = Imaging.getBufferedImage(chartaFile);

        BufferedImage image = ImageIO.read(multipartFile.getInputStream());
        BufferedImage fragment = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics fragmentGraphics = fragment.getGraphics();
        /** удалить*/
        fragmentGraphics.setColor(Color.YELLOW);
        fragmentGraphics.fillRect(0, 0, width, height);
        /***/
        fragmentGraphics.drawImage(image, 0, 0, null);

        Graphics chartaGraphics = chartaImage.getGraphics();
        chartaGraphics.drawImage(fragment, x, y, null);
        ImageIO.write(chartaImage, "jpg", chartaFile);

        chartaImage.flush();
        fragment.flush();
        chartaFile = null;
        fragment = null;
        System.gc();
    }

    public byte[] getRestoredPartOfCharta(String id, int x, int y, int width, int height) throws Exception {
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();

        File chartaFile = new File(chartaService.findByFileUUID(id).getFilePath());
        BufferedImage charta = Imaging.getBufferedImage(chartaFile);
        int widthCharta = charta.getWidth();
        int heightCharta = charta.getHeight();

        BufferedImage restoredPartOfCharta = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        if (x + width > widthCharta || y + height > heightCharta) {
            charta = charta.getSubimage(x, y, widthCharta - x, heightCharta - y);

            Graphics restoredPartOfChartaGraphics = restoredPartOfCharta.getGraphics();
            /** удалить*/
            restoredPartOfChartaGraphics.setColor(Color.YELLOW);
            restoredPartOfChartaGraphics.fillRect(0, 0, width, height);
            /***/
            restoredPartOfChartaGraphics.drawImage(charta, 0, 0, null);
        } else {
            restoredPartOfCharta = charta.getSubimage(x, y, width, height);
        }

        ImageIO.write(restoredPartOfCharta, "bmp", imageBytes);

        charta.flush();
        restoredPartOfCharta.flush();
        charta = null;
        restoredPartOfCharta = null;
        System.gc();
        return imageBytes.toByteArray();
    }

    public static void compareAndWriteToFile(BufferedImage image, String filePath) throws IOException {
        File compressedImageFile = new File(filePath);
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
//        param.setCompressionQuality(0.05F);
//        param.setCompressionType("BI_RGB");
        writer.write(null, new IIOImage(image, null, null), param);

        os.close();
        ios.close();
        writer.dispose();

        image = null;
        System.gc();
    }
}
