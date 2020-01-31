package boot.batch.junit.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroBackupRepository extends JpaRepository<HeroBackup, Long> {
}
