import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Mandelbrot {


    public Mandelbrot() {
        // TODO Auto-generated constructor stub
    }

    public int calculateColor (double x, double y, int maxIter) {
        ComplexNumber number = new ComplexNumber(x, y);
        ComplexNumber z = number;
        int i;
        for (i=0; i<maxIter; i++){
            z = z.multiply(z).add(number);
            if (z.absolute()>2.0){
                Color c = new Color(149+106*i/maxIter,53*i/maxIter,83+42*i/maxIter);
                return c.getRGB();
            } // 255 255   0 żółty
            //// 149  53  83 fioletowy
        }
        return 0x953553; //hexadecymalny fioletowy
    }

    public BufferedImage simulation (int pxW, int pxH, double minW, double maxW, double minH, double maxH, int maxIter) {
        BufferedImage bufferedImage = new BufferedImage(pxW, pxH, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        double dx = (maxW - minW) / (pxW - 1);
        double dy = (maxH - minH) / (pxH - 1);

        for (int x=0; x<pxW; x++){
            for (int y=0; y<pxH; y++){
                double myX = minW + x*dx;
                double myY = maxH - y*dy;
                int color = calculateColor(myX, myY, maxIter);
                bufferedImage.setRGB(x, y, color);
            }
        }
        File outputfile = new File("image"+String.valueOf(pxW)+".jpg");
        try {
            ImageIO.write(bufferedImage, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    public void averageTime (int[] N, int[] sizes) {
        double [] times = new double [sizes.length];
        for (int i=0; i<sizes.length; i++) {
            System.out.println(sizes[i]);
            long start = System.nanoTime();
            for (int j=0; j<N[i]; ++j) {
                this.simulation(sizes[i],sizes[i],-2.1,0.6,-1.2,1.2,200);
            }
            long end = System.nanoTime();
            times[i] = (end-start)/1e9/N[i];
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("times.txt"));
            for (int i=0; i<sizes.length; i++) {
                writer.write(String.valueOf(sizes[i]*sizes[i])+"\t"+String.valueOf(times[i])+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int[] sizes = new int[] {32,64,128,256,512,1024,2048,4096,8192};
        int[] repeats = new int[] {500,500,500,300,200,100,50,20,10}; // różne liczby powtórek monieważ największy obrazek generuje się 80s

        Mandelbrot m = new Mandelbrot();
        //m.simulation(1024,1024,-2.1,0.6,-1.2,1.2,200);
        m.averageTime(repeats,sizes);
    }

}
