package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.dto.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Long register(PostDTO boardDTO);

    PostDTO read(Long id);

    Page<PostDTO> list(Pageable pageable);

    PostDTO update(Long id, PostDTO boardDTO);

    Long delete(Long id);
}
