package com.example.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O atributo título é Obrigatório!")
    @Size(min = 5, max = 100, message = "O atributo title deve conter no mínimo 10 e no máximo 1000 caracteres")
    private String title;

    @NotBlank(message = "O atributo postText é Obrigatório!")
    @Size(min = 10, max = 1000, message = "O atributo postText deve conter no mínimo 10 e no máximo 1000 caracteres")
    private String postText;

    @UpdateTimestamp
    private LocalDateTime publishDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return postText;
    }

    public void setText(String postText) {
        this.postText = postText;
    }

    public LocalDateTime getDate() {
        return publishDate;
    }

    public void setDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }
}
