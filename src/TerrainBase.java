import java.awt.event.KeyEvent;
import java.io.File;  // Import the File class
import java.io.IOException;
import java.util.Scanner; // Import the Scanner class to read text files
import javax.swing.*;
import java.awt.*;

public class TerrainBase
{
    public static double[][] pointArray = null;
    public static int[][] colorArray = null;
    public static double[][] pArray = null;
    public static int[][] edgeList = null;  
    public static int[][] faceList = null;  
    public static double[][] faceCentroids;
    int prevMouseX;

    public static boolean shadePrinted = false;
    public int framebuffer[][][];
    public int shadeBuffer[][][];
    public Transform t;
    public LineBase lines;
    public static int closestCentroid = 0;
    
    public TerrainBase() throws IOException
    {
    	t = new Transform();
        framebuffer = new int[3][1024][1300];
        shadeBuffer = new int[3][1024][1300];
    	createPointArray();
        faceCentroids = new double[4][faceList.length];
        lines = new Lines();

//        t.rotateY(-90);
//        pArray = t.multiplyMatrices(t.transform, pArray);

        t.rotateX(90);
        pArray = t.multiplyMatrices(t.transform, pArray);

        t.scale(50,50,50);
        pArray = t.multiplyMatrices(t.transform, pArray);

        t.translate(400, 200, 100);
        pArray = t.multiplyMatrices(t.transform, pArray);

        faceCentroids = t.CalculateFaceCentroids(pArray, faceList);
        t.CalculateNormals(pArray, faceList);


        ShadeTerrain();
    }

    public static void createPointArray() throws IOException
    {
        File myObj = new File("src/plane.txt");
        Scanner myReader = new Scanner(myObj);
        String temp = "";
        temp = myReader.nextLine();
        int l = Integer.parseInt(temp.substring(temp.indexOf(" ")+1, temp.length())); // get num verts
       
        // verts and colors
        pointArray = new double[l][4];
        colorArray = new int[l][3];

        for (int i = 0; i < pointArray.length; i++) // get verts and colors
        {
            temp = myReader.nextLine();
            int c1 = temp.indexOf(","); // 1st comma in line
            int c2 = temp.indexOf(",", c1+1); // 2nd comma by looking for one after first comma
            int c3 = temp.indexOf(",", c2+1); // 3rd comma
            int c4 = temp.indexOf(",", c3+1); // 4th comma
            int c5 = temp.indexOf(",", c4+1); // 5th comma
            
            pointArray[i][0] = Double.parseDouble(temp.substring(0, c1)); // X - line start to c1
            pointArray[i][1] = Double.parseDouble(temp.substring(c1+1, c2)); // Y - c1+1 to c2
            pointArray[i][2] = Double.parseDouble(temp.substring(c2+1, c3)); // c2+1 to c3
            pointArray[i][3] = 1.0; // homogeneous coord

            colorArray[i][0] = Integer.parseInt(temp.substring(c3+2, c4)); // R - c3+1 to c4
            colorArray[i][1] = Integer.parseInt(temp.substring(c4+2, c5)); // G - c4+1 to c5
            colorArray[i][2] = Integer.parseInt(temp.substring(c5+2, temp.length())); // c5+1 to line end
        }

        pArray = new double[4][l]; 
        for (int i = 0; i < pointArray.length; i++) // order points as a matrix
        {            
            pArray[0][i] = pointArray[i][0];
            pArray[1][i] = pointArray[i][1];
            pArray[2][i] = pointArray[i][2];
            pArray[3][i] = 1.0;
        }

        // edges
        temp = myReader.nextLine(); 
        edgeList = new int[Integer.parseInt(temp.substring(temp.indexOf(" ")+1, temp.length()))][2];

        for (int i = 0; i < edgeList.length; i++) // get edges
        {
            temp = myReader.nextLine();
            int c1 = temp.indexOf(",");
            edgeList[i][0] = Integer.parseInt(temp.substring(0, c1));
            edgeList[i][1] = Integer.parseInt(temp.substring(c1 + 2, temp.length()));
        }

        // faces
        temp = myReader.nextLine(); 
        int numFaces = Integer.parseInt(temp.substring(temp.indexOf(" ")+1, temp.length()));

        temp = myReader.nextLine(); 
        int numPointsInFace = Integer.parseInt(temp.substring(0, temp.indexOf(",")));

        faceList = new int[numFaces][numPointsInFace]; // we read in numPoints, but it assumes 5 points or less. We could change this to a loop to accept any numPoints

        for (int i = 0; i < faceList.length; i++) 
        {           
            int c1 = temp.indexOf(",");
            int c2 = temp.indexOf(",", c1+1); // 2nd comma by looking for one after first comma
            int c3 = temp.indexOf(",", c2+1); // 3rd comma
            int c4 = temp.indexOf(",", c3+1); // 4th comma
            int c5 = temp.indexOf(",", c4+1); // 5th comma     

            faceList[i][0] = Integer.parseInt(temp.substring(c1 + 2, c2));
            faceList[i][1] = Integer.parseInt(temp.substring(c2 + 2, c3));
            faceList[i][2] = Integer.parseInt(temp.substring(c3 + 2, c4));
            faceList[i][3] = Integer.parseInt(temp.substring(c4 + 2, c5));
            faceList[i][4] = Integer.parseInt(temp.substring(c5 + 2, temp.length()));
            
            temp = myReader.nextLine();
        }
        
        myReader.close();
    }

    public static void printPArray()
    {
    	System.out.println("Vertex List");
        for(int i = 0; i < pArray.length; i++)
        {
            for (int j =0; j < pArray[i].length; j++)
            {
                System.out.print(pArray[i][j]+" ");
            }
            System.out.println("");
        }
    }

    public void RefreshTerrain(int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean ctrlPressed) throws IOException
    {
        framebuffer = new int[3][1024][1300];
        shadeBuffer = new int[3][1024][1300];
        faceCentroids = t.CalculateFaceCentroids(pArray, faceList);
        t.CalculateNormals(pArray, faceList);

        // terrain handler
        int distance = Integer.MAX_VALUE;
        int closestCentroidX = 0;
        int closestCentroidY = 0;


        for (int i=0; i < faceCentroids[0].length; i++) // find closest centroid x
        {
            if ( Math.abs(mouseX - faceCentroids[0][i]) < distance)
            {
                distance = (int) Math.abs(mouseX - faceCentroids[0][i]);
                closestCentroidX = i;
            }
        }

        distance = Integer.MAX_VALUE;

        for (int i=0; i < faceCentroids[1].length; i++) // find closest centroid y
        {
            if ( Math.abs(mouseY - faceCentroids[1][i]) < distance)
            {
                distance = (int) Math.abs(mouseY - faceCentroids[1][i]);
                closestCentroidY = i;
            }
        }

        closestCentroid = closestCentroidX + closestCentroidY;

        System.out.println("closest centroid height: " + pArray[1][faceList[closestCentroid][0]]);

        for (int i=0; i < faceList[0].length-2; i++) // translate each point in the face which has the centroid
        {
            if (!shiftPressed) {
                pArray[2][faceList[closestCentroid][i]] -= 20;
                //pArray[2][faceList[closestCentroid][i + 1]] -= 20;
                //pArray[1][faceList[closestCentroid][i - 1]] -= 20;
            }
            else
            {
                pArray[2][faceList[closestCentroid][i]] += 20;
                //pArray[2][faceList[closestCentroid][i + 1]] -= 20;
            }

        }

        faceCentroids = t.CalculateFaceCentroids(pArray, faceList);
        t.CalculateNormals(pArray, faceList);

        LineBase.RGBColor c0 = null;
        LineBase.RGBColor c1 = null;
        LineBase.RGBColor c2 = new LineBase.RGBColor(255,255,255);

        ShadeTerrain();
    }

    ///////////////////////////
    ///////////////////////////
    ///////////////////////////
    ///////////////////////////
    public void RefreshTerrain(KeyEvent ke) throws IOException
    {
        System.out.println("rotate");
        framebuffer = new int[3][1024][1300];

        if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            printPArray();
            double[] axis = {0,1,0};
            double[] temp = {650,500,100,1};
            pArray = t.ArbitraryRotation(pArray, temp, axis, 10);
        }
        if (ke.getKeyCode() == KeyEvent.VK_LEFT)
        {
            printPArray();
            double[] axis = {0,1,0};
            double[] temp = {650,500,100,1};
            pArray = t.ArbitraryRotation(pArray, temp, axis, -10);
        }
        if (ke.getKeyCode() == KeyEvent.VK_UP)
        {
            printPArray();
            t.translate(0, 10, 0);
            pArray = t.multiplyMatrices(t.transform, pArray);
            t.rotateX(-10);
            pArray = t.multiplyMatrices(t.transform, pArray);
        }
        if (ke.getKeyCode() == KeyEvent.VK_DOWN)
        {
            printPArray();
            t.translate(0, -10, 0);
            pArray = t.multiplyMatrices(t.transform, pArray);
            t.rotateX(10);
            pArray = t.multiplyMatrices(t.transform, pArray);
        }

        faceCentroids = t.CalculateFaceCentroids(pArray, faceList);
        t.CalculateNormals(pArray, faceList);
        System.out.println("");


        ShadeTerrain();
    }

    ///////////
    ///////////
    public void ShadeTerrain() throws IOException {
        LineBase.RGBColor c0 = null;
        LineBase.RGBColor c1 = null;
        LineBase.RGBColor c2 = new LineBase.RGBColor(255,255,255);
        // for each face
        //for (int i = 0; i < faceList.length - (faceList.length-1); i++)
        for (int i = 0; i < faceList.length; i++)
        {
            // for each vert in face
            for(int j=0; j < faceList[0].length-1; j++)
            {
                c0 = new LineBase.RGBColor(255, 0, 255); // purple

                lines.BresenhamFormRGB((int) pArray[0][faceList[i][j]], (int) pArray[1][faceList[i][j]], (int) pArray[0][faceList[i][j + 1]], (int) pArray[1][faceList[i][j + 1]], framebuffer, c0, c0);
//                lines.BresenhamFormRGB((int) pArray[0][faceList[i][j]], (int) pArray[1][faceList[i][j]], (int) pArray[0][faceList[i][j + 1]], (int) pArray[1][faceList[i][j + 1]], shadeBuffer, c0, c0);
            }

//            //// shading
//            for (int j = 0; j < shadeBuffer[0].length; j++)
//            {
//                for (int k = 0; k < shadeBuffer[0][0].length-2; k++)
//                {
//                    if ( shadeBuffer[0][j][k] > 0 ) // has color
//                    {
//                        if (shadeBuffer[0][j][k+1] < 1 ) // next does not have color
//                        {
//                            int min = 200;
//                            int max = 1000;
//                            int range = max - min;
//                            int input = (int)faceCentroids[1][closestCentroid];
//                            int correctedStartValue = input - min;
//                            int percentage = (correctedStartValue * 100) / range;
//
//                            int value = 255 * percentage;
//
////                            int value = (int)( 255 * ( 1 / (faceCentroids[1][closestCentroid]) ) );
//
////                            if (value > 255) value=255;
////                            if (value < 0) value=0;
//
//                            shadeBuffer[0][j][k+1] = value; // red
//                            shadeBuffer[1][j][k+1] = 0; // green
//                            shadeBuffer[2][j][k+1] = value; // blue
//                        }
//                        else
//                            break;
//                    }
//                }
//            }
//
//            // copy sb into fb
//            for (int j = 0; j < shadeBuffer[0].length; j++)
//            {
//                for (int k = 0; k < shadeBuffer[0][0].length - 2; k++)
//                {
//                    if (shadeBuffer[0][j][k] > 10)
//                    {
//                        framebuffer[0][j][k] = shadeBuffer[0][j][k];
//                        framebuffer[1][j][k] = shadeBuffer[1][j][k];
//                        framebuffer[2][j][k] = shadeBuffer[2][j][k];
//                    }
//                }
//            }
//
//            // clear sb
//            for (int j = 0; j < shadeBuffer[0].length; j++)
//            {
//                for (int k = 0; k < shadeBuffer[0][0].length - 2; k++)
//                {
//                    shadeBuffer[0][j][k] = 0;
//                    shadeBuffer[1][j][k] = 0;
//                    shadeBuffer[2][j][k] = 0;
//                }
//            }

            // normals
            if( t.faceNormals[2][i] < 0 ) // check z normal facing camera
            {
                //draw face normals
                for (int k = 0; k < t.faceNormals[0].length; k++)
                {
                    double endx = faceCentroids[0][i] + t.faceNormals[0][i] * 5;
                    double endy = faceCentroids[1][i] + t.faceNormals[1][i] * 5;
                    lines.BresenhamFormRGB((int)faceCentroids[0][i], (int)faceCentroids[1][i], (int)endx, (int)endy, framebuffer, c2, c2);
                }
            }
        }
        //System.out.println((int)( 255 * ( 1 / (.01 * faceCentroids[1][closestCentroid]) ) ));
        LineBase.ImageWriteRGB(framebuffer, "src/terrain.png"); // export
    }
}