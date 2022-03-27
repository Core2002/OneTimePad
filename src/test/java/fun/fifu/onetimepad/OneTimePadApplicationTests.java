package fun.fifu.onetimepad;

import com.google.gson.Gson;
import fun.fifu.onetimepad.collection.FullBinaryTree;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class OneTimePadApplicationTests {

    @Test
    void contextLoads() {
        FullBinaryTree<Integer> i = new FullBinaryTree<>(4);
        System.out.println(Arrays.toString(i.getLayerIndex(4)));
    }
}
