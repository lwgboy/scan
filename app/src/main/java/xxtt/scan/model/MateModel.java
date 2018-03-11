package xxtt.scan.model;

import net.tsz.afinal.annotation.sqlite.Id;

/**
 * Created by zhushuyan on 2018/3/3.
 */

public class MateModel {
    public MateModel() {
    }

    public MateModel(int id, String name,String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    @Id
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;
}
