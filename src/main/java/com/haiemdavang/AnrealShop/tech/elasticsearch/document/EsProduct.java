package com.haiemdavang.AnrealShop.tech.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "products", createIndex = false)
public class EsProduct {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, fielddata = true)
    private String name;

    @Field(name = "sort_description", type = FieldType.Text)
    private String sortDescription;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Search_As_You_Type)
    private String suggest;

    @Field(name = "url_slug", type = FieldType.Keyword)
    private String urlSlug;

    @Field(type = FieldType.Long)
    private Long price;

    @Field(name = "discount_price", type = FieldType.Long)
    private Long discountPrice;

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(name = "thumbnail_url", type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(name = "created_at", type = FieldType.Date)
    private Instant createdAt;

    @Field(name = "updated_at", type = FieldType.Date)
    private Instant updatedAt;

    @Field(type = FieldType.Integer)
    private Integer sold;

    @Field(type = FieldType.Long)
    private Long revenue;

    @Field(name = "average_rating", type = FieldType.Half_Float)
    private Float averageRating;

    @Field(name = "total_reviews", type = FieldType.Integer)
    private Integer totalReviews;

    @Field(type = FieldType.Boolean)
    private Boolean visible;

    @Field(name = "restrict_status", type = FieldType.Keyword)
    private String restrictStatus;

    @Field(type = FieldType.Object)
    private EsShop shop;

    @Field(name = "category_id", type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Nested)
    private List<EsAttribute> attributes;
}
