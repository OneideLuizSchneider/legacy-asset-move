package com.legacy.asset.move.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Integer id;
    private String oldPath;
    private String newPath;
}
