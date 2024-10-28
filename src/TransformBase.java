import java.io.File;  // Import the File class
import java.io.IOException;
import java.util.Scanner; // Import the Scanner class to read text files
//import java.util.concurrent.ThreadLocalRandom;

public class TransformBase
{
    public static double[][] pointArray = null;
    public static int[][] colorArray = null;
    public static double[][] pArray = null;
    public static int[][] edgeList = null;  
    public static int[][] faceList = null;  

    public static boolean shadePrinted = false;

    public static void createPointArray() throws IOException
    {
        File myObj = new File("D:\\Google Drive\\Documents\\School\\2022 Fall\\Transform\\src\\cube.txt");
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

    public static void main (String [] args) throws IOException
    {
        int framebuffer[][][] = new int[3][512][512];
        int shadeBuffer[][][] = new int[3][512][512];
        createPointArray();
        printPArray();
        Transform t = new Transform();
        System.out.println("");
        
        double[] axis = {1,1,1};
        pArray = t.ArbitraryRotation(pArray, t.objCentroid, axis, 57);
        System.out.println("Arbitrary Rotation");
        printPArray();
        System.out.println("");

        t.scale(100,100,100);
        pArray = t.multiplyMatrices(t.transform, pArray);
        System.out.println("Scale");
        printPArray();
        System.out.println("");

        t.translate(256, 256, 256);
        pArray = t.multiplyMatrices(t.transform, pArray);
        System.out.println("Translate");
        printPArray();
        System.out.println("Cube Centroid is now " + t.objCentroid[0] + ", " + t.objCentroid[1] + ", " + t.objCentroid[2]);
        //t.CalculateFaceCentroids(pArray, faceList);
        t.CalculateNormals(pArray, faceList);
        System.out.println("");
        
    	// start render
        LineBase lines = new Lines(); 
        LineBase.RGBColor c0 = null;
        LineBase.RGBColor c1 = null;
        LineBase.RGBColor c2 = new LineBase.RGBColor(255,255,255);

        for (int i = 0; i < faceList.length; i++) // loop through faces
        { 
            System.out.println("Face " + i);

            //reset shadebuffer
            for (int j = 0; j < shadeBuffer.length; j++) 
            {
                for (int k = 0; k < shadeBuffer[j].length; k++) 
                {
                    for (int l = 0; l < shadeBuffer[j][k].length; l++) 
                    {
                        shadeBuffer[j][k][l] = -1;
                    }
                }
            }
            
            // loop through verts
            for(int j=0; j < faceList[0].length-1; j++) 
            {
                c0 = new LineBase.RGBColor(colorArray[faceList[i][j]][0], colorArray[faceList[i][j]][1], colorArray[faceList[i][j]][2]);

                System.out.println(faceList[i][j] + " r " + c0.R + " g " + c0.G + " b " + c0.B);
                
            	c1 = new LineBase.RGBColor(colorArray[faceList[i][j+1]][0], colorArray[faceList[i][j+1]][1], colorArray[faceList[i][j+1]][2]);

                System.out.println(faceList[i][j+1] + " r " + c1.R + " g " + c1.G + " b " + c1.B);

                if( t.faceNormals[2][i] < 0 ) // check z normal facing camera 
                {
                    // draw face one line at a time to shadebuffer
                    if (j < faceList[0].length-1) 
                    {
                    	lines.BresenhamFormRGB((int)pArray[0][faceList[i][j]], (int)pArray[1][faceList[i][j]], (int)pArray[0][faceList[i][j+1]], (int)pArray[1][faceList[i][j+1]], shadeBuffer, c0, c1);
                    }

                    // draw edges
                    if (j < faceList[0].length-1) // for colors
                    {
                    	lines.BresenhamFormRGB((int)pArray[0][faceList[i][j]], (int)pArray[1][faceList[i][j]], (int)pArray[0][faceList[i][j+1]], (int)pArray[1][faceList[i][j+1]], framebuffer, c0, c1);
                    }
                    
                    // // draw face normals 
                    // for (int k = 0; k < t.faceNormals[0].length; k++)
                    // { 
                    //     double endx = t.faceCentroids[0][i] + t.faceNormals[0][i] * 80;
                    //     double endy = t.faceCentroids[1][i] + t.faceNormals[1][i] * 80;
                    //     lines.BresenhamFormRGB((int)t.faceCentroids[0][i], (int)t.faceCentroids[1][i], (int)endx, (int)endy, framebuffer, c2, c2);
                    // }
                }
            }

            // // once a face is done, copy into framebuffer
            // for (int j = 0; j < shadeBuffer.length; j++) 
            // {
            //     for (int k = 0; k < shadeBuffer[j].length; k++) 
            //     {
            //         for (int l = 0; l < shadeBuffer[j][k].length; l++) 
            //         {
            //             shadeBuffer[j][k][l] = -1;
            //         }
            //     }
            // }
        }
        
        //draw cube centroid
        //lines.BresenhamFormRGB((int)t.objCentroid[0], (int)t.objCentroid[1], (int)t.objCentroid[0], (int)t.objCentroid[1], framebuffer, c2, c2);

        LineBase.ImageWriteRGB(framebuffer, "src/cube.png"); // export
    }
}