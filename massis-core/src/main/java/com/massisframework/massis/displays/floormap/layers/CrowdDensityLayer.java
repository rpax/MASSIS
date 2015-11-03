package com.massisframework.massis.displays.floormap.layers;

import static java.awt.Color.HSBtoRGB;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.HashMap;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Floor;

/**
 * Shows efficiently the crowd density of the floor as a heat map
 *
 * @author rpax
 *
 */
public class CrowdDensityLayer extends FloorMapLayer {

    /**
     * An arbitrary image size. It must be a tradeoff between resolution, CPU
     * and memory. 512 it is fair enough.
     */
    private static double IMG_MAX_SIZE = 512;

    public CrowdDensityLayer(boolean enabled)
    {
        super(enabled);

    }
    /**
     * Map linking the layers with their corresponding floors
     */
    private final HashMap<Floor, BufferedImage> densityImages = new HashMap<>();

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        /**
         * The factor with wich this floor must be scaled in order to fit in the
         * image
         */
        final double scaleFactor = IMG_MAX_SIZE
                / ((f.xlength > f.ylength) ? f.xlength : f.ylength);
        /**
         * drawing width
         */
        final int width = (int) (f.xlength * scaleFactor);
        /**
         * drawing height
         */
        final int height = (int) (f.ylength * scaleFactor);
        /*
         * Recover the image from the cached map. It is not there already,
         * creates a new one
         */
        if (!densityImages.containsKey(f))
        {
            this.densityImages.put(f, new BufferedImage(width, height,
                    TYPE_INT_ARGB));
        }
        /**
         * The image to be drawn in the graphics object
         */
        final BufferedImage image = this.densityImages.get(f);
        /*
         * Heatmap data
         * 
         * this array will be used with 2 purposes:
         * 
         * 1.- storing the occupation in each cell of the floor 2.- As image
         * data.
         * 
         * In order to accomplish this, two passes must be done: One for
         * computing the occupation and another for transforming the occupation
         * factor into RGB values.
         */
        final int[] data = ((DataBufferInt) image.getRaster().getDataBuffer())
                .getData();
        /*
         * Initially there is nothing in the map
         */
        Arrays.fill(data, 0);
        /**
         * Maximum value : The RGB colors of each pixel will be based on this
         * value. ( 0 : min, max: max)
         */
        int max = Integer.MIN_VALUE;

        for (LowLevelAgent a : f.getPeople())
        {
            /*
             * Furniture should not be counted in the occupation.
             */
            if (!a.isDynamic())
            {
                continue;
            }
            /*
             * Translation of the real coordinates of the agent into the images'
             * coordinates
             */
            final int y1 = (int) ((a.getY() - f.minY) * scaleFactor);
            final int x1 = (int) ((a.getX() - f.minX) * scaleFactor);
            /*
             * Occupation radius : 500 cm => 5 meters.
             */
            final int radius = (int) (5 * 100 * scaleFactor);
            /*
             * Bounds fixing.
             * 
             * Instead of checking every time if the coordinates are
             * inside/outside of the map, the x,y coordinates are "fixed"
             * before,(e.g <=0 -->0 , >=length --> length-1).
             */
            int xLeft = fixXBounds(x1 - radius, width);
            int yTop = fixYBounds(y1 - radius, height);
            int xRight = fixXBounds(x1 + radius, width);
            int yBot = fixYBounds(y1 + radius, height);
            /**
             * Current factor of occupation. Starts with 1. every iteration, the
             * radius it is lower, and the occupation value of the cell must be
             * higher.
             */
            int currentFactor = 1;
            while (xLeft <= xRight && yTop <= yBot)
            {
                int x = xLeft;
                int y = yTop;
                // Left to Right
                for (; x < xRight; x++)
                {
                    max = Math.max(data[y * width + x] += currentFactor, max);
                }
                // top to bottom
                for (; y < yBot; y++)
                {
                    max = Math.max(data[y * width + x] += currentFactor, max);
                }
                // Right to left
                for (; x > xLeft; x--)
                {
                    max = Math.max(data[y * width + x] += currentFactor, max);
                }
                // bottom to top
                for (; y > yTop; y--)
                {
                    max = Math.max(data[y * width + x] += currentFactor, max);
                }

                yTop++;
                yBot--;
                xLeft++;
                xRight--;
                currentFactor++;

            }
        }
        /*
         * Second pass : The occupation data is replaced with an rgb value
         */
        for (int i = 0; i < data.length; i++)
        {
            data[i] = HSBtoRGB((1 - data[i] * 1f / max) * 0.7f, 1, 1);
        }
        /*
         * Finally, draw the image data.
         */
        g.drawImage(image, f.minX, f.minY, (f.maxX - f.minX),
                (f.maxY - f.minY), null);
    }

    private int fixXBounds(int x, int width)
    {
        if (x < 0)
        {
            return 0;
        }
        if (x >= width)
        {
            return width - 1;
        }
        return x;
    }

    private int fixYBounds(int y, int height)
    {
        if (y < 0)
        {
            return 0;
        }
        if (y >= height)
        {
            return height - 1;
        }
        return y;
    }

    @Override
    public String getName()
    {
        return "Crowd density";
    }
}
