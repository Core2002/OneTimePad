package fun.fifu.onetimepad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fun.fifu.onetimepad.collection.FullBinaryTree;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@SpringBootTest
class OneTimePadApplicationTests {
    @Autowired
    WebApplicationContext context;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void fullBinaryTreeTest() {
        FullBinaryTree<Integer> i = new FullBinaryTree<>(4);
        System.out.println(Arrays.toString(i.getLayerIndex(4)));
    }

    @Test
    void encryptionAndDecryptionTest() throws Exception {
        var key = encryption("小白最帅", 16);
        System.out.println("加密后密文；" + key);
        var plainText = decryption(key);
        System.out.println("解密后明文：" + plainText);
    }

    String encryption(String plainText, Integer groupNumber) throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/encryption")
                .param("plainText", plainText)
                .param("groupNumber", groupNumber.toString())
        ).andReturn();
        var jsonText = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var json = gson.fromJson(jsonText, JsonObject.class);
        return json.get("keyGroupText").getAsString();
    }

    String decryption(String hexKeyGroupJson) throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/decryption")
                .param("hexKeyGroupJson", hexKeyGroupJson)
        ).andReturn();
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
