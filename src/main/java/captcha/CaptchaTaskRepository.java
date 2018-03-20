package captcha;

import org.springframework.data.repository.CrudRepository;

public interface CaptchaTaskRepository extends CrudRepository<CaptchaTask, String> {
}
