package com.zhishen.aixuexue.weight.filterview;

import java.io.Serializable;
import java.util.List;

public class FilterData implements Serializable {

    private List<FilterTwoEntity> nears;
    private List<FilterTwoEntity> language;
    private List<FilterEntity> sorts;


    public List<FilterTwoEntity> getNears() {
        return nears;
    }

    public void setNears(List<FilterTwoEntity> nears) {
        this.nears = nears;
    }

    public List<FilterTwoEntity> getLanguage() {
        return language;
    }

    public void setLanguage(List<FilterTwoEntity> language) {
        this.language = language;
    }

    public List<FilterEntity> getSorts() {
        return sorts;
    }

    public void setSorts(List<FilterEntity> sorts) {
        this.sorts = sorts;
    }

}
