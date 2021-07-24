package com.tangyh.lamp.file.dao;

import com.tangyh.basic.base.mapper.SuperMapper;
import com.tangyh.lamp.file.entity.File;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 增量文件上传日志
 * </p>
 *
 * @author tangyh
 * @date 2021-06-30
 * @create [2021-06-30] [tangyh] [初始创建]
 */
@Repository
public interface FileMapper extends SuperMapper<File> {

}
