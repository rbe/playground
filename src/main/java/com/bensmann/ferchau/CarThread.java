package com.bensmann.ferchau;

/**
 * carThread -> CarThread (classes shall start with uppercase letter)
 * implements Thread -> extends Thread
 * price cannot be final
 * Thread#run: Console output did not use variable brand, try/catch for Thread.sleep
 * psvm: Instance of CarThread need two parameters, threads are started through call to .start() not .run()
 */
public class CarThread extends Thread {

    final String brand;
    final String model;
    double price;

    public CarThread(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public void run() {
        while (true) {
            System.out.printf("hello my name is %s%n", this.brand);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static void main(String[] args) {
        new CarThread("Audi", "A8").start();
        new CarThread("BMW", "X6").start();
        System.out.println("CarThreads are running... ");
    }

}
