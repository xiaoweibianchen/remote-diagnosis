package com.remote.diagnosis.service.result;

import java.io.Serializable;

public class MessageResponse
  implements Serializable
{
  public static final String SUCCESS_CODE = "00000";
  private String responseModule;

  @ResponseCode
  private String responseCode = "00000";

  @ResponseDesc
  private String responseDesc;
  private static final long serialVersionUID = -4925664451632459176L;

  public String getResponseModule()
  {
    return this.responseModule;
  }

  public void setResponseModule(String responseModule)
  {
    this.responseModule = responseModule;
  }

  public String getResponseCode()
  {
    return this.responseCode;
  }

  public void setResponseCode(String responseCode)
  {
    this.responseCode = responseCode;
  }

  public String getResponseDesc()
  {
    return this.responseDesc;
  }

  public void setResponseDesc(String responseDesc)
  {
    this.responseDesc = responseDesc;
  }

  public boolean isSuccessful()
  {
    return "00000".equals(this.responseCode);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder("MessageResponse{");
    sb.append("responseModule='").append(this.responseModule).append('\'');
    sb.append(", responseCode='").append(this.responseCode).append('\'');
    sb.append(", responseDesc='").append(this.responseDesc).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
