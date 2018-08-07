package com.sgnbs.crud.modelpagelist.container;

import com.github.pagehelper.Page;

import java.util.List;

public interface PageDataContainerintf {

    Object pack(Page page);
    Object pack(List list);
}
