package at.aau.se2.singletest;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MatriculationNumberServiceTest {

    MatriculationNumberService service;

    @Before
    public void init() {
        service = new MatriculationNumberService();
    }


    @Test
    public void send() {
        //byte[] req = new BigInteger("1234578").toByteArray();
        String req = "12345678";
        String res = service.sendSync(req);
        assertNotNull(res);
        System.out.println(res);
    }

    // Matrikelnummer sortieren, wobei zuerst alle geraden,
    // dann alle ungeraden Ziffern gereiht sind
    // (erst die geraden, dann alle ungeraden Ziffern aufsteigend sortiert)
    @Test
    public void testSortedNumbersAsStrings() {
        service.repository.add(1);
        service.repository.add(1000_0000);
        service.repository.add(2000_0001);
        service.repository.add(2);

        assertEquals(Arrays.asList("00000002", "10000000", "00000001", "20000001"),
                service.sortedNumbersAsStrings());
    }
}