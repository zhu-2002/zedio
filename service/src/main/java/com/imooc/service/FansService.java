package com.imooc.service;


public interface FansService {
    /*
    * 关注
    * */
    public void createFollow( String myId , String vlogerId ) ;

    /*
     * 取关
     * */
    public void cancelFollow( String myId , String vlogerId ) ;

    /**
     * 查询用户是否关注博主
     */
    public boolean queryFollow(String myId, String vlogerId);

}
