/*
 * twinder
 * CS6650 assignment API
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * SwipeDetails
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-02-09T05:23:36.672Z[GMT]")
public class SwipeDetails {
  @SerializedName("swiper")
  private String swiper = null;

  @SerializedName("swipee")
  private String swipee = null;

  @SerializedName("comment")
  private String comment = null;

  public SwipeDetails swiper(String swiper) {
    this.swiper = swiper;
    return this;
  }

   /**
   * Get swiper
   * @return swiper
  **/
  @Schema(example = "233", description = "")
  public String getSwiper() {
    return swiper;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public SwipeDetails swipee(String swipee) {
    this.swipee = swipee;
    return this;
  }

   /**
   * Get swipee
   * @return swipee
  **/
  @Schema(example = "21345", description = "")
  public String getSwipee() {
    return swipee;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public SwipeDetails comment(String comment) {
    this.comment = comment;
    return this;
  }

   /**
   * Get comment
   * @return comment
  **/
  @Schema(example = "you are not my type, loser", description = "")
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SwipeDetails swipeDetails = (SwipeDetails) o;
    return Objects.equals(this.swiper, swipeDetails.swiper) &&
        Objects.equals(this.swipee, swipeDetails.swipee) &&
        Objects.equals(this.comment, swipeDetails.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(swiper, swipee, comment);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SwipeDetails {\n");
    
    sb.append("    swiper: ").append(toIndentedString(swiper)).append("\n");
    sb.append("    swipee: ").append(toIndentedString(swipee)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
