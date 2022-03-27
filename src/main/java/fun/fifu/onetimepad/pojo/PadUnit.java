package fun.fifu.onetimepad.pojo;

import io.vavr.collection.Tree;
import lombok.Data;

@Data
public class PadUnit {
    String encryptionType;
    String plaintext;
    Tree<String> keyGroup;
}