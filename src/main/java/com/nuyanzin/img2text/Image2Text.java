package com.nuyanzin.img2text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Image2Text
 */
public class Image2Text {
  private Image2Text() {
  }

  public static void main(String[] args) {
    Map<String, Object> argMap = new HashMap<>();
    String filename = null;
    String output = null;
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
      case "-w": argMap.put("-w", Integer.parseInt(args[i + 1]));
                 i++;
                 break;
      case "-h": argMap.put("-h", Integer.parseInt(args[i + 1]));
                 i++;
                 break;
      case "-r": argMap.put("-r", Float.parseFloat(args[i + 1]));
                 i++;
                 break;
      case "-c": argMap.put("-c", Integer.parseInt(args[i + 1]));
                 i++;
                 break;
      default:   if (filename == null) {
          filename = args[i];
        } else {
          output = args[i];
        }
      }
    }

    if (filename == null) {
      System.err.println("Usage ./image2text [-flags] filename");
      System.exit(1);
    }
    final BufferedImage img;
    try {
      img = ImageIO.read(new File(filename));
    } catch (IOException e) {
      System.err.println("Could no open image " + filename);
      throw new RuntimeException(e);
    }
    int scaledWidth = img.getWidth();
    int scaledHeight = img.getHeight();
    if (argMap.containsKey("-r")) {
      scaledWidth = (int) (scaledWidth * (Float) argMap.get("-r"));
      scaledHeight = (int) (scaledHeight * (Float) argMap.get("-r"));
    } else if (argMap.containsKey("-h") || argMap.containsKey("-w")) {
      scaledHeight = argMap.containsKey("-h")
          ? (Integer) argMap.get("-h") : scaledHeight;
      scaledWidth = argMap.containsKey("-w")
          ? (Integer) argMap.get("-w") : scaledWidth;
    }

    BufferedImage outputImage = new BufferedImage(scaledWidth,
        scaledHeight, img.getType());

    Graphics2D g2d = outputImage.createGraphics();
    g2d.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
    g2d.dispose();

    int minGray = 255;
    int maxGray = 0;
    int[][] pixels = new int[outputImage.getWidth()][outputImage.getHeight()];
    for (int i = 0; i < outputImage.getWidth(); i++) {
      for (int j = 0; j < outputImage.getHeight(); j++) {
        final int pixelGrayColor = pixel2avgGrayScale(outputImage.getRGB(i, j));
        minGray = minGray == 0 ? 0 : Math.min(minGray, pixelGrayColor);
        maxGray = maxGray == 255 ? 255 : Math.max(maxGray, pixelGrayColor);
        pixels[i][j] = pixelGrayColor;
      }
    }
    final int contrastCoef = argMap.get("-c") == null
        ? 1 : (Integer) argMap.get("-c");
    final int avg = (minGray + maxGray + 1) / (2 * contrastCoef);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < outputImage.getHeight(); i++) {
      for (int j = 0; j < outputImage.getWidth(); j++) {
        sb.append(avg > pixels[j][i] ? 1 : 0);
        if (j == outputImage.getWidth() - 1) {
          sb.append("\n");
        }
      }
    }
    if (output == null) {
      System.out.println(sb);
    } else {
      try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
        bw.append(sb.toString()).flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static int pixel2avgGrayScale(int pixelRGBColor) {
    final int red = (pixelRGBColor >> 16) & 0xFF;
    final int green = (pixelRGBColor >> 8) & 0xFF;
    final int blue = pixelRGBColor & 0xFF;
    return (red + green + blue) / 3;
  }
}
