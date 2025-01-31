package fr.efrei.pokemon_tcg.services.implementations;

import fr.efrei.pokemon_tcg.dto.CapturePokemon;
import fr.efrei.pokemon_tcg.dto.DresseurDTO;
import fr.efrei.pokemon_tcg.models.Dresseur;
import fr.efrei.pokemon_tcg.models.Pokemon;
import fr.efrei.pokemon_tcg.repositories.DresseurRepository;
import fr.efrei.pokemon_tcg.services.IDresseurService;
import fr.efrei.pokemon_tcg.services.IPokemonService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class DresseurServiceImpl implements IDresseurService {
	private final DresseurRepository repository;
	private final IPokemonService pokemonService;

	public DresseurServiceImpl(DresseurRepository repository, IPokemonService pokemonService) {
		this.repository = repository;
		this.pokemonService = pokemonService;
	}

	@Override
	public List<Dresseur> findAll() {
		return this.repository.findAllByDeletedAtNull();
	}

	@Override
	public Dresseur findById(String uuid) {
		return this.repository.findById(uuid).orElseThrow(() -> new RuntimeException("Dresseur introuvable avec l'UUID : " + uuid));
	}

	@Override
	public void capturerPokemon(String uuid, CapturePokemon capturePokemon) {
		Dresseur dresseur = this.findById(uuid);
		Pokemon pokemon = this.pokemonService.findById(capturePokemon.getUuid());
		dresseur.getPokemonList().add(pokemon);
		this.repository.save(dresseur);
	}

	@Override
	public void create(DresseurDTO dresseurDTO) {
		Dresseur dresseur = new Dresseur();
		dresseur.setNom(dresseurDTO.getNom());
		dresseur.setPrenom(dresseurDTO.getPrenom());
		dresseur.setDeletedAt(null);
		this.repository.save(dresseur);
	}

	@Override
	public boolean update(String uuid, DresseurDTO dresseurDTO) {
		return false;
	}

	@Override
	public boolean delete(String uuid) {
		Dresseur dresseur = this.findById(uuid);
		dresseur.setDeletedAt(LocalDateTime.now());
		this.repository.save(dresseur);
		return true;
	}

	@Override
	public void ouvrirBooster(String uuid) {
		Dresseur dresseur = this.findById(uuid);

		if (dresseur == null) {
			throw new RuntimeException("Dresseur introuvable avec l'UUID : " + uuid);
		}

		List<Pokemon> allPokemon = pokemonService.findAll();

		if (allPokemon.size() < 5) {
			throw new RuntimeException("Pas assez de Pokémon disponibles pour ouvrir un booster !");
		}

		Collections.shuffle(allPokemon);
		List<Pokemon> boosterPokemon = allPokemon.subList(0, 5);
		dresseur.getPokemonList().addAll(boosterPokemon);
		this.repository.save(dresseur);
	}
}