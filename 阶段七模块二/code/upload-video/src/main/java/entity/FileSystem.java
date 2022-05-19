package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @BelongsProject:entity
 * @Author: JinHua
 * @CreateTime:2022/5/16
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileSystem implements Serializable {
    private String fileId;
    private String filePath;
    private String fileName;
}
