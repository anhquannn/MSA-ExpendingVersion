package com.market.MSA.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long categoryId;

  String name;
  String description;

  @ManyToOne
  @JoinColumn(name = "parent_category_id") // Khóa ngoại liên kết tới chính Category
  Category parentCategory;

  @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Category> subCategories; // Danh sách danh mục con

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Product> products;
}
