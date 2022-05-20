package com.visma.shop.warehouse.userActivity;

import com.visma.shop.warehouse.item.Item;
import com.visma.shop.warehouse.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer bought;
    @OneToOne
    private Item item;
    @ManyToOne
    private User user;

}
