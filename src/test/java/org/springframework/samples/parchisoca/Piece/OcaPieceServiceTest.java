package org.springframework.samples.parchisoca.Piece;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisoca.piece.Colour;
import org.springframework.samples.parchisoca.piece.OcaPiece;
import org.springframework.samples.parchisoca.piece.OcaPieceRepository;
import org.springframework.samples.parchisoca.piece.OcaPieceService;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class OcaPieceServiceTest {

    @Autowired(required = false)
    OcaPieceRepository or;

    @Autowired(required = false)
    OcaPieceService os;

    @Test
    void shouldfindOcaPieceById(){
        OcaPiece o = os.findOcaPieceById(1);
        List<OcaPiece> ls = new ArrayList<>();
        ls.add(o);
        assertThat(ls.size() ==1);
        
    }

    @Test
    void shouldSaveNewPieceOca(){
        OcaPiece o = new OcaPiece();
        o.setId(10);
        o.setXPosition(5);
        o.setYPosition(6);
        o.setColour(Colour.RED);

        os.save(o);

        OcaPiece o10 = os.findOcaPieceById(1);
        List<OcaPiece> ls = new ArrayList<>();
        ls.add(o10);
        assertThat(ls.size() ==1);




    }

    
}
