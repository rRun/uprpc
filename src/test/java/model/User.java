package model;

import java.io.Serializable;

public class User implements Serializable{
  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  private int age;
}
