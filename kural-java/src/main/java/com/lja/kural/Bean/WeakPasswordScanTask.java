package com.lja.kural.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class WeakPasswordScanTask extends BaseScanTask{
    private List<String> serviceTypeList;   // 爆破服务(List)，如FTP、SSH等
    private String serviceTypes;            // 爆破服务(String)
    private String usernameFilePath;        // 用户名字典路径
    private String passwordFilePath;        // 密码字典路径
    private Boolean useDefaultDict;         // 是否使用默认密码字典
    // Getters and Setters

}
