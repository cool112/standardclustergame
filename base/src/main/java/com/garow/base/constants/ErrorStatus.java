package com.garow.base.constants;

public interface ErrorStatus {
int SUC = 0;
/** 权限-无效session*/
int INVALID_SESSION = 1;
/** 没有授权*/
int NO_AUTH = 2;
/**参数错误*/
int INVALID_PARAMS = 3;
/**重复请求*/
int REPEAT_REQUEST = 4;
}
