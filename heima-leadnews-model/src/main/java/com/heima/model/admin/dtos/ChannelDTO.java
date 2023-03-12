package com.heima.model.admin.dtos;

import com.heima.model.common.dtos.PageRequestDTO;
import lombok.Data;

@Data
public class ChannelDTO extends PageRequestDTO {

        private  String name;

        private  Integer status;

}
