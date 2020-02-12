package net.mpimedia.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.Dto;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.Event;
import net.mpimedia.entity.Institution;
import net.mpimedia.entity.Member;
import net.mpimedia.entity.Position;
import net.mpimedia.entity.Post;
import net.mpimedia.entity.Program;
import net.mpimedia.entity.RegisteredRequest;
import net.mpimedia.entity.Section;
import net.mpimedia.entity.User;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;

	  private User user ;
      private Division division ;
      private Event event ;
      private Institution institution ;
      private Position position ;
      private Post post ;
      private  Program program ;
      private  Section section ;
      private Member member ;
      private long divisionId ;

      private String entity ;
      private Filter filter ;

      private int year ;
      private int month ;

      //public
      private String requestId ;
      
      //msg
      private String destination;
      private String value;
      private String username;
      
      private RegisteredRequest registeredRequest;

}
