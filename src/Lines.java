public class Lines extends LineBase {
    int size = 256;
    int [][] image;

    Lines()
    {
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j<size; j++)
            {
            }
        }
    }

    Lines(int size)
    {
        image = new int [size][size];
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j<size; j++)
            {
            }
        }
    }

    @Override
    public void twoPointForm(int x0, int y0, int x1, int y1, int[][] framebuffer)
            throws NullPointerException, ArrayIndexOutOfBoundsException {
                //the rate of change of x and y are calculates
                //we can use this to generate a variable d where d is a-b where a is the distance from the next point up and b is the distance form the next point down
                //if you multiply this by delta x before assigning it it will poduce an integer value that is postitive or negative
                //if the value is negative then b>a thus it is closer to the upper point, if it is positive then a>b and it is closer to the bottom point
                // then you can decide if the next point in the graph is at which y value based on where it is closer to
                // to calculate the next d you have 2 options based on wheither or not it incramente last time
                //if it didn't then d(k+1) = d(k) - 2*dy
                //if it did then d(k+1) = d(k) - 2(dy-dx)
                size = framebuffer.length;
                int top = y1-y0;
                int bot = x1-x0;
                double slope = (double)top/bot;
                for (int x = 0; x < size; x++)
                {
                    int y = (int)
                    (slope *x - slope*x0 +y0);
                    
                    if (y>-1 && y<256)
                    {
                        framebuffer[x][y] = 255;
                    }
                }
                
    }

    @Override
    public void parametricForm(int x0, int y0, int x1, int y1, int[][] framebuffer)
            throws NullPointerException, ArrayIndexOutOfBoundsException {
        // TODO Auto-generated method stub
        size = framebuffer.length;
        for (int i = 0; i < size; i++)
        {
            int x =(int)( x0 + (x1-x0)*((double)i/256));
            int y =(int)( y0 + (y1-y0)*((double)i/256));
            if (x < framebuffer.length && y < framebuffer.length && x>=0 && y>=0)
            {
                framebuffer[x][y] = 255;
            }

        }
    }

    @Override
    public void BresenhamForm(int x1, int y1, int x2, int y2, int[][] framebuffer)
            throws NullPointerException, ArrayIndexOutOfBoundsException {
        // TODO Auto-generated method stub
        //obtained from http://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
        //origonal and semi functional code for this can be found in the two point form method
         // delta of exact value and rounded value of the dependent variable
         int d = 0;
 
         int dx = Math.abs(x2 - x1);
         int dy = Math.abs(y2 - y1);
  
         int dx2 = 2 * dx; // slope scaling factors to
         int dy2 = 2 * dy; // avoid floating point
  
         int ix = x1 < x2 ? 1 : -1; // increment direction
         int iy = y1 < y2 ? 1 : -1;
  
         int x = x1;
         int y = y1;
  
         if (dx >= dy) {
             while (true) {
                 framebuffer[x][y] = 255;
                 if (x == x2)
                     break;
                 x += ix;
                 d += dy2;
                 if (d > dx) {
                     y += iy;
                     d -= dx2;
                 }
             }
         } else {
             while (true) {
                framebuffer[x][y] = 255;
                 if (y == y2)
                     break;
                 y += iy;
                 d += dx2;
                 if (d > dy) {
                     x += ix;
                     d -= dy2;
                 }
             }
         }
    }

    @Override
    public void BresenhamFormRGB(int x1, int y1, int x2, int y2, int[][][] framebuffer, LineBase.RGBColor C0, LineBase.RGBColor C1) throws NullPointerException, ArrayIndexOutOfBoundsException 
    {
        // TODO Auto-generated method stub
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        int r0 = C0.R, g0 = C0.G, b0 = C0.B;
        int r1 = C1.R, g1 = C1.G, b1 = C1.B;
        int pixNum = 0;
        if (Math.abs(x2-x1) >= Math.abs(y2-y1))
        {
            pixNum = Math.abs(x2-x1);
        }
        else
        {
            pixNum = Math.abs(y2-y1);
        }

        float ri;
        if (r0 > r1)
        {
            ri = (float)(r1-r0)/(float)pixNum;
        }
        else
        {
            ri = (float)-1*(r1-r0)/(float)pixNum;
        }
        float gi;
        if (g0 > g1)
        {
            gi = (float)(g1-g0)/(float)pixNum;
        }
        else
        {
            gi = (float)-1*(g1-g0)/(float)pixNum;
        }
        float bi;
        if (b0 > b1)
        {
            bi = (float)(b1-b0)/(float)pixNum;
        }
        else
        {
            bi = (float)-1*(b1-b0)/(float)pixNum;
        }
        float rv = r0, gv = g0, bv = b0;

        //calculate how much it needs to incrimant each color at each point
        //then calculate the direction it needs to incriment
        //probs use bresenham to decide when to increase or decrease color
        // add this to below while loop so that it changes the line slightly as it goes

        if (dx >= dy) 
        {
            while (true) 
            {
                framebuffer[0][y][x] = (int)rv;
                framebuffer[1][y][x] = (int)gv;
                framebuffer[2][y][x] = (int)bv;
                rv += ri;
                gv += gi;
                bv += bi;

                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) 
                {
                    y += iy;
                    d -= dx2;
                }
            }
        } 
        else 
        {
            while (true) 
            {
                framebuffer[0][y][x] = (int)rv;
                framebuffer[1][y][x] = (int)gv;
                framebuffer[2][y][x] = (int)bv;
                rv += ri;
                gv += gi;
                bv += bi;
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) 
                {
                    x += ix;
                    d -= dy2;
                }
            }
        }
    } 

    public void BresenhamFormRGB2(int x1, int y1, int x2, int y2, int[][][] framebuffer, LineBase.RGBColor c0, LineBase.RGBColor c1) throws NullPointerException, ArrayIndexOutOfBoundsException 
    {   
        // TODO Auto-generated method stub
        //obtained from http://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
        //origonal and semi functional code for this can be found in the two point form method
        // delta of exact value and rounded value of the dependent variable
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        if (dx >= dy) 
        {
            while (true) 
            {    
                int p = 2*dy-dx;
                framebuffer[0][x][y] = c0.R*(1-d)+c1.R*d;
                framebuffer[1][x][y] = c0.G*(1-d)+c1.G*d;
                framebuffer[2][x][y] = c0.B*(1-d)+c1.B*d;
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) 
                {
                    y += iy;
                    d -= dx2;
                }
            }
        } 
        else 
        {
            while (true) 
            {
                int p = 2*dy-dx;
                framebuffer[0][x][y] = c0.R*(1-d)+c1.R*d;
                framebuffer[1][x][y] = c0.G*(1-d)+c1.G*d;
                framebuffer[2][x][y] = c0.B*(1-d)+c1.B*d;
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) 
                {
                    x += ix;
                    d -= dy2;
                }
            }
        }
    }
}
