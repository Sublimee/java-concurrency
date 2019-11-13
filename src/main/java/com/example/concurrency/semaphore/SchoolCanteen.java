package com.example.concurrency.semaphore;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class SchoolCanteen {

    // Место за столом свободно - true, занято - false
    private static final Boolean[] DINING_PLACES;
    // Устанавливаем флаг "справедливый", в таком случае метод
    // aсquire() будет раздавать разрешения в порядке очереди
    private static final Semaphore SEMAPHORE;

    static {
        DINING_PLACES = new Boolean[5];
        SEMAPHORE = new Semaphore(DINING_PLACES.length, true);
        Arrays.fill(DINING_PLACES, Boolean.TRUE);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            new Thread(new Student(i)).start();
            Thread.sleep(400);
        }
    }

    public static class Student implements Runnable {
        private int studentId;

        Student(int studentId) {
            this.studentId = studentId;
        }

        @Override
        public void run() {
            System.out.printf("Студент %d вошел в столовую.\n", studentId);
            try {
                // acquire() запрашивает доступ к следующему за вызовом этого метода блоку кода,
                // если доступ не разрешен, поток вызвавший этот метод блокируется до тех пор,
                // пока семафор не разрешит доступ
                SEMAPHORE.acquire();

                int diningPlace = -1;

                // Ищем свободное место и занимаем его
                synchronized (DINING_PLACES) {
                    for (int i = 0; i < DINING_PLACES.length; i++)
                        if (DINING_PLACES[i]) {             // Если место свободно
                            DINING_PLACES[i] = false;       // занимаем его
                            diningPlace = i;                // Наличие свободного места, гарантирует семафор
                            System.out.printf("Студент %d занял место %d.\n", studentId, i);
                            break;
                        }
                }

                Thread.sleep(5000); // Обедаем

                synchronized (DINING_PLACES) {
                    DINING_PLACES[diningPlace] = true; // Освобождаем место
                }

                // release() освобождает ресурс
                SEMAPHORE.release();
                System.out.printf("Студент %d покинул столовую.\n", studentId);
            } catch (InterruptedException e) {
            }
        }

    }

}