package lb2;

import lb1.OneLb;
import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TwoLb {

    public static void run(String[] args) throws FileNotFoundException {
        int[] arrayX= new int[1];
        int root = 0;
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        do {
            if(rank==0){
                Scanner scanner = new Scanner(new FileInputStream(new File("textIn.txt")));
                arrayX[0]=scanner.nextInt();
            } else {
            }
            MPI.COMM_WORLD.Bcast(arrayX,0,1,MPI.INT,root);
            System.out.println("Process " + rank + " got " + arrayX[0]);
        } while (arrayX[0]<=0);
        MPI.Finalize();
    }
}