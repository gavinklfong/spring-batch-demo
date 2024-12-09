package space.gavinklfong.demo.batch.job.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import space.gavinklfong.demo.batch.dto.Person;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(final Person person) {
        final String firstName = person.firstName().toUpperCase();
        final String lastName = person.lastName().toUpperCase();

        final Person transformedPerson = new Person(firstName, lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }

}
