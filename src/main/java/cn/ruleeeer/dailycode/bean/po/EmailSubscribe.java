package cn.ruleeeer.dailycode.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ruleeeer
 * @date 2021/10/4 14:35
 */
@Data
@Builder
public class EmailSubscribe {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String email;

    @Builder.Default
    private LocalDateTime subscribeTime = LocalDateTime.now();

}
