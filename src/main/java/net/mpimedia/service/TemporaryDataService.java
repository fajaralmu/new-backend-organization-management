package net.mpimedia.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.Institution;
import net.mpimedia.entity.User;
import net.mpimedia.repository.DivisionRepository;

@Service
@Slf4j
public class TemporaryDataService {

	@Autowired
	private DivisionRepository divisionRepository;

	private List<Division> divisions = new ArrayList<>();

	@PostConstruct
	public void init() {
		log.info("Refresh divisions");

		this.divisions = divisionRepository.findAll();

		log.info("End Refresh Divisions");
	}

	public List<Division> getDivision(User user) {

		if (null == user || null == user.getInstitution()) {
			return new ArrayList<>();
		}

		if (this.divisions.size() == 0) {
			refresh();
			return divisionRepository.findByInstitution(user.getInstitution());

		}
		return getByInstitution(user.getInstitution());
	}

	public void refresh() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				init();

			}
		});
		thread.start();
	}

	private List<Division> getByInstitution(Institution institution) {

		List<Division> divisionFiltered = new ArrayList<>();

		for (Division division : divisions) {
			if (division.getInstitution().getId().equals(institution.getId())) {
				divisionFiltered.add(division);
			}
		}

		return divisionFiltered;
	}

	private Division filterById(long id) {

		for (Division division : divisions) {
			if (division.getId().equals(id)) {
				return division;
			}
		}

		return null;
	}

	public Division getById(long divisionId) {

		if (divisions.size() == 0) {
			try {
				return divisionRepository.findById(divisionId).get();
			} catch (Exception e) {
				return null;
			}
		}

		return filterById(divisionId);
	}

}
