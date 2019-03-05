package example;

import lb1.OneLb;
import lb2.TwoLb;
import mpi.MPI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
//        OneLb.run(args);
        TwoLb.run(args);
//        TwoLb.run2(args);
    }
}