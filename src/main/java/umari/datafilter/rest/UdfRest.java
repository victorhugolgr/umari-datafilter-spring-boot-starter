package umari.datafilter.rest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umari.datafilter.core.Filterable;
import umari.datafilter.service.UdfTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@RequestMapping("/api/udf")
@RestController
@ConditionalOnWebApplication
public class UdfRest {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UdfRest.class);

    @Autowired
    private UdfTemplate udfTemplate;

    @Autowired
    private EntityManagerFactory emf;

    @PostConstruct
    private void init() {
        log.info("Initialized.");
    }

    @PostMapping("/filter/{entity}")
    public ResponseEntity<Page<?>> filter(@PathVariable String entity, @RequestBody Filterable filterable, Pageable pageable) {
        Class<?> entityClass = emf.getMetamodel().getEntities().stream()
                .filter(entityType -> entityType.getName().equals(entity))
                .map(entityType -> entityType.getJavaType())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Entitade %s não encontrada no projeto.", entity)));
        return ResponseEntity.ok(udfTemplate.filter(entityClass, filterable, pageable));
    }

}