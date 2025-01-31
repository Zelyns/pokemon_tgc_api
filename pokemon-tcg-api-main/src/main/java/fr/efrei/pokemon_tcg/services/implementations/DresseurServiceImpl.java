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
import java.util.*;
import java.util.stream.Collectors;

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
		return this.repository.findById(uuid)
				.orElseThrow(() -> new RuntimeException("Dresseur introuvable avec l'UUID : " + uuid));
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

		Map<Integer, List<Pokemon>> pokemonParRarete = allPokemon.stream()
				.collect(Collectors.groupingBy(Pokemon::getRarete));

		Random random = new Random();
		List<Pokemon> boosterPokemon = new ArrayList<>();

		if (pokemonParRarete.containsKey(1) && pokemonParRarete.get(1).size() >= 2) {
			boosterPokemon.add(pokemonParRarete.get(1).get(random.nextInt(pokemonParRarete.get(1).size())));
			boosterPokemon.add(pokemonParRarete.get(1).get(random.nextInt(pokemonParRarete.get(1).size())));
		}

		if (pokemonParRarete.containsKey(2) && !pokemonParRarete.get(2).isEmpty()) {
			boosterPokemon.add(pokemonParRarete.get(2).get(random.nextInt(pokemonParRarete.get(2).size())));
		}

		if (pokemonParRarete.containsKey(3) && !pokemonParRarete.get(3).isEmpty()) {
			boosterPokemon.add(pokemonParRarete.get(3).get(random.nextInt(pokemonParRarete.get(3).size())));
		}

		if (random.nextInt(100) < 10 && pokemonParRarete.containsKey(4) && !pokemonParRarete.get(4).isEmpty()) {
			boosterPokemon.add(pokemonParRarete.get(4).get(random.nextInt(pokemonParRarete.get(4).size())));
		}
		else if (random.nextInt(100) < 5 && pokemonParRarete.containsKey(5) && !pokemonParRarete.get(5).isEmpty()) {
			boosterPokemon.add(pokemonParRarete.get(5).get(random.nextInt(pokemonParRarete.get(5).size())));
		}
		else {
			if (pokemonParRarete.containsKey(3) && !pokemonParRarete.get(3).isEmpty()) {
				boosterPokemon.add(pokemonParRarete.get(3).get(random.nextInt(pokemonParRarete.get(3).size())));
			}
		}

		dresseur.getPokemonList().addAll(boosterPokemon);

		this.repository.save(dresseur);
	}
}