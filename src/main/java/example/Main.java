package example;

import lb1.OneLb;
import lb2.TwoLb;
import lb3.ThreeLb;
import mpi.MPI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
//        OneLb.run(args);
//        TwoLb.run(args);
        ThreeLb.run(args);
    }
}