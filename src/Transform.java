//import java.math.*;

public class Transform extends TransformBase {

    public double[][] transform = new double[4][4];
    public double[] objCentroid = new double[4];
    //public double[][] faceCentroids;
    public double[][] faceNormals;

    Transform()
    {
        for (int i = 0; i < transform.length; i++)
        {
            for (int j = 0; j < transform.length; j++)
            {
                transform[i][j] = 1.0;
            }
        }
    }

    public double[][] multiplyMatrices(double[][] x, double[][] y)
    {
        if (x == null || y == null)
        {
            throw new IllegalArgumentException("null operand");
        }

        if (x[0].length != y.length)
        {
            throw new IllegalArgumentException("incorrect sizes " + x[0].length + " & " + y.length);
        }

        double [][] r = new double[x.length][y[0].length];
        for (int i = 0; i < x.length; i++)
        {
            for (int j = 0; j < y[0].length; j++)
            {
                r[i][j] = 0.0;
                for (int k = 0; k < x[i].length; k++)
                {
                    r[i][j] += x[i][k] * y[k][j];
                }
            }
        }

        CalculateCubeCentroid(r);

        return r;
    }

    public double[] CrossProduct(double u[], double v[])
    {
        double[] cross = new double[4];

        cross[0] = u[1] * v[2] - u[2] * v[1];
        cross[1] = u[2] * v[0] - u[0] * v[2];
        cross[2] = u[0] * v[1] - u[1] * v[0];
        cross[3] = 1; 
        
        return cross;
    }

    public double[] ScalarMultiply(double[] v, double s)
    {
        v[0] *= s;
        v[1] *= s;
        v[2] *= s;
        
        return v;
    }

    public double[] Normalize(double v[])
    {
        double[] unitVector = new double[4];

        // {x / sqrt(x^2 + y^2 + z^2), ..., ..., 1}
        double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

        unitVector[0] = v[0] / length;
        unitVector[1] = v[1] / length;
        unitVector[2] = v[2] / length;
        unitVector[3] = 1; 
        
        return unitVector;
    }

    public void resetMatrix()
    {
        for (int i = 0; i < transform.length; i++)
        {
            for (int j = 0; j < transform.length; j++)
            {
                transform[i][j] = 1.0;
            }
        }
    }

    public void CalculateCubeCentroid(double[][] vl)
    {
    	double[] temp = null;
    	temp = new double[vl.length];

        for(int i=0; i < vl[0].length; i++)
        {
            temp[0] += vl[0][i]; // sum x of each pt in obj
            temp[1] += vl[1][i]; // sum y of each pt in obj
            temp[2] += vl[2][i]; // sum z of each pt in obj
        }

        temp[0] /= 8; // TODO: replace const with num vertices
        temp[1] /= 8; // avg y of each pt in obj
        temp[2] /= 8; // avg z of each pt in obj
        temp[3] = 1.0;  

        objCentroid = temp;
    }

    public double[][] CalculateFaceCentroids(double[][] vl, int[][] fl)
    {
        double[][] currentFace = null;
        currentFace = new double[4][fl[0].length-1]; // 4 by numPoints
        double[][] faceCentroids = new double[4][fl.length]; // 4 by numFaces

        for(int i=0; i < fl.length; i++) // each face in array
        {
            for(int j=0; j < fl[i].length-1; j++) // each point in face
            {
                for(int k=0; k < 4; k++) // each cartesian in point
                {
                    currentFace[k][j] = vl[k][fl[i][j]]; // get vertice from faceList and cartesian from vertexList
                }

                faceCentroids[0][i] += currentFace[0][j]; // sum x of each pt in face
                faceCentroids[1][i] += currentFace[1][j]; // sum y of each pt in face
                faceCentroids[2][i] += currentFace[2][j]; // sum z of each pt in face

                //System.out.println("Face " + i + " Point " + j + " is vertice " + fl[i][j] + ". It's Cartesians are: " + currentFace[0][j] + ", " + currentFace[1][j] + ", " + currentFace[2][j] + ", " + currentFace[3][j]);
            }
            //System.out.println("");
        }

        for(int i=0; i < fl.length; i++) // each face in array
        {
            faceCentroids[0][i] /= fl[i].length-1;  // avg x of each face
            faceCentroids[1][i] /= fl[i].length-1;  // avg y of each face
            faceCentroids[2][i] /= fl[i].length-1;  // avg z of each face
            faceCentroids[3][i] = 1.0;

            //System.out.println("Face " + i + " Centroid is: " + faceCentroids[0][i] + "x, " + faceCentroids[1][i] + "y, " + faceCentroids[2][i]  + "z, " + faceCentroids[3][i]);
        }

        return faceCentroids;
    }

    public void CalculateNormals(double[][] vl, int[][] fl)
    {
        double[][] currentFace = null;
        currentFace = new double[4][fl[0].length-1]; // 4 by numPoints
        faceNormals = new double[4][fl.length]; // 4 by numFaces

        for(int i=0; i < fl.length; i++) // each face in array
        {
            for(int j=0; j < fl[i].length-1; j++) // each point in face
            {
                for(int k=0; k < 4; k++) // each cartesian in point
                {
                    currentFace[k][j] = vl[k][fl[i][j]]; // get vertice from faceList and cartesian from vertexList
                }
            }

            double[] u = {currentFace[0][2] - currentFace[0][1],    // p2.x - p1.x
                        currentFace[1][2] - currentFace[1][1],  // p2.y - p1.y 
                        currentFace[2][2] - currentFace[2][1],  // p2.z - p1.z    
                        1};

            double[] v = {currentFace[0][1] - currentFace[0][0],    // p1.x - p0.x
                        currentFace[1][1] - currentFace[1][0],  // p1.y - p0.y 
                        currentFace[2][1] - currentFace[2][0],  // p1.z - p0.z    
                        1};

            double[] normal = Normalize(CrossProduct(u,v));

            faceNormals[0][i] = normal[0];
            faceNormals[1][i] = normal[1];
            faceNormals[2][i] = normal[2];
            faceNormals[3][i] = normal[3];

            //System.out.println("Face " + i + " normal is: " + faceNormals[0][i] + "x, " + faceNormals[1][i] + "y, " + faceNormals[2][i]  + "z, " + faceNormals[3][i]);
        }
    }

    public void scale(double x, double y, double z)
    {
        double[][] temp = {
        {x,0,0,0},
        {0,y,0,0},
        {0,0,z,0},
        {0,0,0,1}};
        transform = temp;
    }

    public void translate(double x, double y, double z)
    {
        double[][] temp = {
            {1,0,0,x},
            {0,1,0,y},
            {0,0,1,z},
            {0,0,0,1}};
        transform = temp;
    }

    public double[][] ArbitraryRotation(double[][] vl, double[] fixedPoint, double[] axis, double deg)
    {
        axis = Normalize(axis);
        double ux = axis[0];
        double uy = axis[1];
        double uz = axis[2];
        double d = Math.sqrt(uy * uy + uz * uz);

        double[] startPos = fixedPoint;

        double[][] finalMatrix = vl;

        if (d != 0) 
        {
            // to origin
            translate(-fixedPoint[0],-fixedPoint[1],-fixedPoint[2]);
            finalMatrix = multiplyMatrices(transform, finalMatrix);

            // rx
            Rx(ux, uy, uz, d);  
            finalMatrix = multiplyMatrices(transform, finalMatrix);

            // ry 
            Ry(ux, uy, uz, d);
            finalMatrix = multiplyMatrices(transform, finalMatrix);

            // rz 
            rotateZ(deg);
            finalMatrix = multiplyMatrices(transform, finalMatrix);

            // -ry 
            nRy(ux, uy, uz, d);
            finalMatrix = multiplyMatrices(transform, finalMatrix);

            // -rx
            nRx(ux, uy, uz, d); 
            finalMatrix = multiplyMatrices(transform, finalMatrix);

            // back to original pos
            translate(startPos[0], startPos[1], startPos[2]);
            finalMatrix = multiplyMatrices(transform, finalMatrix);
        }
        else
        {
            System.out.println("d=0");
        }

        return finalMatrix;
    }

    public void Rx(double ux, double uy, double uz, double d)
    {
        double[][] temp = {
            {1, 0, 0, 0},
            {0, uz/d, -uy/d, 0},
            {0, uy/d, uz/d, 0},
            {0, 0, 0, 1}};
        transform = temp;
    }

    public void nRx(double ux, double uy, double uz, double d)
    {
        double[][] temp = {
            {1, 0, 0, 0},
            {0, -(uz/d), -(-uy/d), 0},
            {0, -(uy/d), -(uz/d), 0},
            {0, 0, 0, 1}};
        transform = temp;
    }

    public void Ry(double ux, double uy, double uz, double d)
    {
        double[][] temp = {
            {d, 0, -ux, 0},
            {0, 1, 0, 0},
            {ux, 0, d, 0},
            {0, 0, 0, 1}};
        transform = temp;
    }

    public void nRy(double ux, double uy, double uz, double d)
    {
        double[][] temp = {
            {d, 0, ux, 0},
            {0, 1, 0, 0},
            {-ux, 0, d, 0},
            {0, 0, 0, 1}};
        transform = temp;
    }

    public void rotateX(double deg)
    {
        double[][] temp = {
            {1,0,0,0},
            {0,Math.cos(Math.toRadians(deg)),-Math.sin(Math.toRadians(deg)),0},
            {0,Math.sin(Math.toRadians(deg)),Math.cos(Math.toRadians(deg)),0},{0,0,0,1}};
        transform = temp;
    }

    public void rotateY(double deg)
    {
        double[][] temp = 
            {{Math.cos(Math.toRadians(deg)),0,Math.sin(Math.toRadians(deg)),0},
            {0,1,0,0},
            {-Math.sin(Math.toRadians(deg)),0,Math.cos(Math.toRadians(deg)),0},
            {0,0,0,1}};
        transform = temp;
    }

    public void rotateZ(double deg)
    {
        double[][] temp = 
        {{Math.cos(Math.toRadians(deg)),-Math.sin(Math.toRadians(deg)),0,0},
        {Math.sin(Math.toRadians(deg)),Math.cos(Math.toRadians(deg)),0,0},
        {0,0,1,0},
        {0,0,0,1}};
        transform = temp;
    }
    
    public void test()
    {
        double[][] a = {{1,1,1},{2,2,2},{3,3,3}};
        double[][] b = {{1,1,1},{2,2,2},{3,3,3}};
        double[][] c = multiplyMatrices(a, b);
        for(int i = 0; i < c.length; i++)
        {
            for (int j =0; j < c[i].length; j++)
            {
                System.out.println(c[i][j]);
            }
        }
    }
}
