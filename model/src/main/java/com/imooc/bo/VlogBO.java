package com.imooc.bo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogBO {
    @NonNull
    private String id;
    @NonNull
    private String vlogerId;
    private String url;
    private String cover;
    private String title;
    private Integer width;
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
}