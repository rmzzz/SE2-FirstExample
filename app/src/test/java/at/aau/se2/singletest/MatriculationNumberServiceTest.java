package at.aau.se2.singletest;

import org.junit.Before;
import org.junit.Test;

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
}