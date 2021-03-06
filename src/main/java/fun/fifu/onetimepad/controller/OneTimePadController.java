package fun.fifu.onetimepad.controller;

import cn.hutool.core.util.HexUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import fun.fifu.onetimepad.collection.FullBinaryTree;
import fun.fifu.onetimepad.pojo.PadUnit;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@RestController
public class OneTimePadController {
    static SecureRandom secureRandom = new SecureRandom(SecureRandom.getSeed(4096));

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Resource
    private MongoTemplate mongoTemplate;

    @PostMapping("/api/encryption")
    public PadUnit encryptionApi(String plainText, Integer groupNumber) {
        byte[][] encryption = encryption(plainText, groupNumber);
        PadUnit padUnit = new PadUnit();
        padUnit.setEncryptionType("一次一密");
        padUnit.setPlaintext(plainText);
        padUnit.setKeyGroup(encryption);
        padUnit.setKeyGroupText(arrayToJsonArray(encryption));
        mongoTemplate.insert(padUnit);
        return padUnit;
    }

    @PostMapping("/api/decryption")
    public String decryptionApi(String hexKeyGroupJson) {
        JsonArray keyStringList = gson.fromJson(hexKeyGroupJson, JsonArray.class);
        byte[][] keys = new byte[keyStringList.size()][];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = HexUtil.decodeHex(keyStringList.get(i).getAsString());
        }
        return new String(decryption(keys));
    }

    /**
     * 将一个明文使用一次一密加密成两个密钥
     *
     * @param plainText 明文
     * @return 密钥组
     */
    public Tuple2<byte[], byte[]> encryption(byte[] plainText) {
        byte[] bytes_key0 = secureRandom.generateSeed(plainText.length);
        byte[] bytes_key1 = new byte[bytes_key0.length];
        for (int i = 0; i < bytes_key0.length; i++) bytes_key1[i] = (byte) (plainText[i] ^ bytes_key0[i]);
        return Tuple.of(bytes_key0, bytes_key1);
    }

    private Tuple2<byte[], byte[]> encryption(String plainText) {
        return encryption(plainText.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将一个明文使用一次一密加密成groupNumber个密钥
     *
     * @param plainText   明文
     * @param groupNumber 密钥组数
     * @return 密钥组
     */
    private byte[][] encryption(String plainText, int groupNumber) {
        // 假定 groupNumber = 8
        byte[][] keys = new byte[groupNumber][];
        // 画颗树
        FullBinaryTree<byte[]> tree = new FullBinaryTree<>(FullBinaryTree.getLayer(groupNumber));
        // 置入明文到根节点
        tree.getPojo(1).setValue(plainText.getBytes(StandardCharsets.UTF_8));
        // 加密
        for (int p = 1; p <= tree.getNodeTotal(tree.getLayerTotal() - 1); p++) {
            Tuple2<byte[], byte[]> layer2 = encryption(tree.getPojo(p).getValue());
            tree.getLeftDaughter(p).setValue(layer2._1);
            tree.getRightDaughter(p).setValue(layer2._2);
        }
        // 封装密钥
        int[] layerIndex = tree.getLayerIndex(tree.getLayerTotal());
        for (int i = 0; i < layerIndex.length; i++) {
            keys[i] = tree.getPojo(layerIndex[i]).getValue();
        }
        return keys;
    }


    /**
     * 将密钥组解密成一个明文
     *
     * @param bytes_key 密钥组
     * @return 明文
     */
    public byte[] decryption(byte[][] bytes_key) {
        // 假定 bytes_key.length = 8
        byte[] plain;
        // 画颗树
        FullBinaryTree<byte[]> tree = new FullBinaryTree<>(FullBinaryTree.getLayer(bytes_key.length));
        // 把密钥组装在到树底
        for (int i = 0; i < bytes_key.length; i++) {
            tree.getPojo(bytes_key.length + i).setValue(bytes_key[i]);
        }
        // 解密
        for (int l = FullBinaryTree.getLayer(bytes_key.length); l > 1; l--) {
            int[] layerIndex = tree.getLayerIndex(l);
            for (int index : layerIndex) {
                FullBinaryTree.Pojo<byte[]> pojo = tree.getPojo(index);
                if (tree.isLeft(pojo.getIndex())) {
                    tree.getFather(pojo.getIndex()).setValue(pojo.getValue());
                } else {
                    tree.getFather(pojo.getIndex()).setValue(decryption(new Tuple2<>(tree.getFather(pojo.getIndex()).getValue(), pojo.getValue())));
                }
            }
        }
        // 取明文
        plain = tree.getPojo(1).getValue();
        return plain;
    }

    /**
     * 将密钥组解密成一个明文
     *
     * @param bytes_key 密钥组
     * @return 明文
     */
    public byte[] decryption(Tuple2<byte[], byte[]> bytes_key) {
        byte[] plain = new byte[bytes_key._1().length];
        for (int i = 0; i < plain.length; i++) plain[i] = (byte) (bytes_key._1()[i] ^ bytes_key._2()[i]);
        return plain;
    }

    public String arrayToJsonArray(byte[][] bytes) {
        var jsonArray = new JsonArray();
        for (byte[] aByte : bytes) jsonArray.add(HexUtil.encodeHexStr(aByte));
        return gson.toJson(jsonArray);
    }

}
