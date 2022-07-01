package yehor.budget.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
public class Category {

    @Id
    @SequenceGenerator(name = "categories_sequence", sequenceName = "categories_category_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_sequence")
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private List<Expense> expenses;
}
