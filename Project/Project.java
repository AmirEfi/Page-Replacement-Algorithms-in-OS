import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Project {

    static int FIFO(Queue<Integer> fifo, int[] arr_fifo, int page_fa_fifo, int table_num, int client_num) {

        if (!fifo.contains(client_num)) {
            page_fa_fifo++;

            if (fifo.size() < table_num) {
                arr_fifo [fifo.size()] = client_num;
                fifo.add(client_num);
                System.out.println("Client number " + client_num + " added to restaurant in FIFO (Tables aren't full yet).");
            }
            else {
                int temp = fifo.peek();
                for (int i = 0; i < fifo.size(); i++) {
                    if (arr_fifo[i] == temp)
                        arr_fifo[i] = client_num;
                }
                fifo.poll();
                fifo.add(client_num);
                System.out.println("Client number " + client_num + " added to restaurant in FIFO (Tables are full).");
            }
        }
        else {
            System.out.println("Client number " + client_num + " is already in restaurant in FIFO.");
        }
        System.out.print("Clients in FIFO: ");
        for (int temp: arr_fifo)
            System.out.print(temp + " ");
        System.out.println();
        System.out.println("=============================================");

        return page_fa_fifo;
    }

    static int LRU (HashSet<Integer> lru, HashMap<Integer, Integer> index_lru, int[] arr_lru, int index_lru_temp, int page_fa_lru, int table_num, int client_num) {

        if(!lru.contains(client_num)) {
            page_fa_lru++;

            if (lru.size() < table_num) {
                arr_lru[lru.size()] = client_num;
                lru.add(client_num);
                System.out.println("Client number " + client_num + " added to restaurant in LRU (Tables aren't full yet).");
                index_lru.put(client_num, index_lru_temp);
            }
            else {
                int temp_lru = Integer.MAX_VALUE, temp_val = Integer.MIN_VALUE;

                for (int temp : lru) {
                    if (index_lru.get(temp) < temp_lru) {
                        temp_lru = index_lru.get(temp);
                        temp_val = temp;
                    }
                }
                int temp = temp_val;
                for (int i = 0; i < lru.size(); i++) {
                    if (arr_lru[i] == temp)
                        arr_lru[i] = client_num;
                }
                lru.remove(temp_val);
                index_lru.remove(temp_val);
                lru.add(client_num);
                index_lru.put(client_num, index_lru_temp);
                System.out.println("Client number " + client_num + " added to restaurant in LRU (Tables are full).");
            }
        }
        else {
            index_lru.put(client_num, index_lru_temp);
            System.out.println("Client number " + client_num + " is already in restaurant in LRU (Recently used updated).");
        }

        System.out.print("Clients in LRU: ");
        for (int temp: arr_lru)
            System.out.print(temp + " ");
        System.out.println();
        System.out.println("=============================================");

        return page_fa_lru;
    }

    static int Second_chance (Queue<Integer> secCha, HashMap<Integer, Boolean> index_secCha, int[] arr_secCha, int page_fa_secCha, int table_num, int client_num) {

        if(!secCha.contains(client_num)) {
            page_fa_secCha++;

            if (secCha.size() < table_num) {
                arr_secCha[secCha.size()] = client_num;
                secCha.add(client_num);
                System.out.println("Client number " + client_num + " added to restaurant in Second-chance (Tables aren't full yet).");
                index_secCha.put(client_num, false);
            }
            else {
                boolean found_victim = false;

                while(!found_victim) {
                    int head = secCha.peek();

                    if (index_secCha.get(head)) {
                        int temp = secCha.poll();
                        index_secCha.remove(head);
                        secCha.add(temp);
                        index_secCha.put(temp,false);
                    }
                    else{
                        int temp = secCha.peek();
                        for (int i = 0; i < secCha.size(); i++) {
                            if (arr_secCha[i] == temp)
                                arr_secCha[i] = client_num;
                        }
                        secCha.poll();
                        index_secCha.remove(head);
                        secCha.add(client_num);
                        index_secCha.put(client_num, false);
                        found_victim = true;
                    }
                }
                System.out.println("Client number " + client_num + " added to restaurant in Second-chance (Tables are full).");
            }
        }
        else {
            index_secCha.put(client_num, true);
            System.out.println("Client number " + client_num + " is already in restaurant in Second-chance (Reference bit updated to 1).");
        }

        System.out.print("Clients in Second-chance: ");
        for (int temp: arr_secCha)
            System.out.print(temp + " ");
        System.out.println();
        System.out.println("=============================================");

        return page_fa_secCha;
    }

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 8080);

        Queue <Integer> fifo = new LinkedList<>();
        HashSet <Integer> lru = new HashSet<>();
        HashMap <Integer, Integer> index_lru = new HashMap<>();
        int index_lru_temp = 0; // find recently used client by help of this
        Queue <Integer> secCha = new LinkedList<>();
        HashMap <Integer, Boolean> index_secCha = new HashMap<>();


        int table_num = 0, client_num = 0;
        int page_fa_fifo = 0, page_fa_lru = 0, page_fa_secCha = 0;

        DataInputStream dis = new DataInputStream(s.getInputStream());
        table_num = dis.readInt();
        System.out.println("Number of Tables: " + table_num);

        int[] arr_fifo = new int[table_num];
        int[] arr_lru = new int[table_num];
        int[] arr_secCha = new int[table_num];

        client_num = dis.readInt();

        while (client_num != 0 ) {

            page_fa_fifo = FIFO(fifo, arr_fifo, page_fa_fifo, table_num, client_num);

            page_fa_lru = LRU(lru, index_lru, arr_lru, index_lru_temp, page_fa_lru, table_num, client_num);
            index_lru_temp++;

            page_fa_secCha = Second_chance(secCha, index_secCha, arr_secCha, page_fa_secCha, table_num, client_num);

            client_num = dis.readInt();

        }

        System.out.println("LRU: " + page_fa_lru + ", FIFO: " + page_fa_fifo + ", Second-chance: " + page_fa_secCha);
        System.out.println("END!");
    }
}
