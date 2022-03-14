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
        compressAndWriteToFile(bufferedImage, filePath);
        Charta charta = new Charta(width, height, fileUUID, filePath);
        chartaService.save(charta);

        bufferedImage.flush();
        bufferedImage = null;
        return fileUUID;
    }

    public void saveRestoredFragmentCharta(String id, int x, int y, int width, int height, MultipartFile multipartFile) throws Exception {
        File chartaFile = new File(chartaService.findByFileUUID(id).getFilePath());

        BufferedImage chartaImage = Imaging.getBufferedImage(chartaFile);
        BufferedImage image = ImageIO.read(multipartFile.getInputStream());
        BufferedImage fragment = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics fragmentGraphics = fragment.getGraphics();
        fragmentGraphics.drawImage(image, 0, 0, null);
        Graphics chartaGraphics = chartaImage.getGraphics();
        chartaGraphics.drawImage(fragment, x, y, null);
        ImageIO.write(chartaImage, "jpg", chartaFile);

        fragmentGraphics.dispose();
        chartaGraphics.dispose();
        chartaImage.flush();
        fragment.flush();
        image.flush();
        fragmentGraphics = null;
        chartaGraphics = null;
        chartaFile = null;
        fragment = null;
        image = null;
    }

    public byte[] getRestoredPartOfCharta(String id, int x, int y, int width, int height) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File chartaFile = new File(chartaService.findByFileUUID(id).getFilePath());
        BufferedImage charta = Imaging.getBufferedImage(chartaFile);
        int widthCharta = charta.getWidth();
        int heightCharta = charta.getHeight();

        BufferedImage restoredPartOfCharta = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        if (x + width > widthCharta || y + height > heightCharta) {
            charta = charta.getSubimage(x, y, widthCharta - x, heightCharta - y);
            Graphics restoredPartOfChartaGraphics = restoredPartOfCharta.getGraphics();
            restoredPartOfChartaGraphics.drawImage(charta, 0, 0, null);
            restoredPartOfChartaGraphics.dispose();
        } else {
            restoredPartOfCharta = charta.getSubimage(x, y, width, height);
        }
        ImageIO.write(restoredPartOfCharta, "bmp", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        charta.flush();
        restoredPartOfCharta.flush();
        charta = null;
        restoredPartOfCharta = null;
        return imageBytes;
    }

    public void deleteCharta(String fileUUID) {
        chartaService.delete(fileUUID);
    }

    private static void compressAndWriteToFile(BufferedImage image, String filePath) throws IOException {
        File compressedImageFile = new File(filePath);
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
        writer.write(null, new IIOImage(image, null, null), param);

        os.close();
        ios.close();
        writer.dispose();
        image = null;
    }
}
