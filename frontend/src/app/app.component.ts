import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

type StatusKind = 'idle' | 'ok' | 'error' | 'loading';

interface Usuario {
  id?: number;
  email: string;
  senha: string;
  role: string;
}

interface Sala {
  id?: number;
  nome: string;
  capacidade: number;
  descricao?: string;
}

interface ReservaUpsertRequest {
  salaId: number;
  usuarioId: number;
  dataReserva: string;
}

interface ReservaResponse {
  id?: number;
  salaId: number;
  usuarioId: number;
  dataReserva: string;
  duracaoHoras: number;
  status: 'ATIVA' | 'CANCELADA';
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  pingStatus: { kind: StatusKind; message: string } = { kind: 'idle', message: '' };
  usuarioStatus: { kind: StatusKind; message: string } = { kind: 'idle', message: '' };
  salaStatus: { kind: StatusKind; message: string } = { kind: 'idle', message: '' };
  reservaStatus: { kind: StatusKind; message: string } = { kind: 'idle', message: '' };

  usuarioForm: Usuario = { email: '', senha: '', role: 'USER' };
  salaForm: Sala = { nome: '', capacidade: 10, descricao: '' };
  reservaForm: ReservaUpsertRequest = {
    salaId: 1,
    usuarioId: 1,
    dataReserva: this.defaultDateTime()
  };

  listTitle = 'Nenhum resultado ainda';
  listOutput = '';

  constructor(private readonly http: HttpClient) {}

  async testarConexao() {
    this.pingStatus = { kind: 'loading', message: 'Testando conexao...' };
    const targets = [
      { name: 'usuarios', url: '/api/usuarios' },
      { name: 'salas', url: '/api/salas' },
      { name: 'reservas', url: '/api/reservas' }
    ];

    const results: string[] = [];
    for (const target of targets) {
      try {
        const res = await fetch(target.url);
        results.push(`${target.name}: ${res.status}`);
      } catch {
        results.push(`${target.name}: offline`);
      }
    }
    const ok = results.every((item) => item.includes('200'));
    this.pingStatus = { kind: ok ? 'ok' : 'error', message: results.join(' | ') };
  }

  criarUsuario() {
    this.usuarioStatus = { kind: 'loading', message: 'Criando usuario...' };
    const payload: Usuario = {
      email: this.usuarioForm.email.trim(),
      senha: this.usuarioForm.senha,
      role: this.usuarioForm.role.trim() || 'USER'
    };
    this.http
      .post<Usuario>('/api/usuarios', payload)
      .subscribe({
        next: (data) => {
          this.usuarioStatus = {
            kind: 'ok',
            message: `Usuario criado (id ${data.id})`
          };
          this.usuarioForm = { email: '', senha: '', role: 'USER' };
        },
        error: (err) => this.setError(this.usuarioStatus, err)
      });
  }

  criarSala() {
    this.salaStatus = { kind: 'loading', message: 'Criando sala...' };
    const payload: Sala = {
      nome: this.salaForm.nome.trim(),
      capacidade: Number(this.salaForm.capacidade),
      descricao: this.salaForm.descricao?.trim() || ''
    };
    this.http
      .post<Sala>('/api/salas', payload)
      .subscribe({
        next: (data) => {
          this.salaStatus = { kind: 'ok', message: `Sala criada (id ${data.id})` };
          this.salaForm = { nome: '', capacidade: 10, descricao: '' };
        },
        error: (err) => this.setError(this.salaStatus, err)
      });
  }

  criarReserva() {
    this.reservaStatus = { kind: 'loading', message: 'Criando reserva...' };
    const payload: ReservaUpsertRequest = {
      salaId: Number(this.reservaForm.salaId),
      usuarioId: Number(this.reservaForm.usuarioId),
      dataReserva: this.toLocalDateTime(this.reservaForm.dataReserva)
    };
    this.http
      .post<ReservaResponse>('/api/reservas', payload)
      .subscribe({
        next: (data) => {
          this.reservaStatus = { kind: 'ok', message: `Reserva criada (id ${data.id})` };
          this.reservaForm = {
            ...this.reservaForm,
            dataReserva: this.defaultDateTime()
          };
        },
        error: (err) => this.setError(this.reservaStatus, err)
      });
  }

  listarUsuarios() {
    this.listar('Usuarios', '/api/usuarios');
  }

  listarSalas() {
    this.listar('Salas', '/api/salas');
  }

  listarReservas() {
    this.listar('Reservas', '/api/reservas');
  }

  private listar(titulo: string, url: string) {
    this.listTitle = `Carregando ${titulo}...`;
    this.listOutput = '';
    this.http.get(url).subscribe({
      next: (data) => {
        this.listTitle = `Lista de ${titulo}`;
        this.listOutput = JSON.stringify(data, null, 2);
      },
      error: (err) => {
        this.listTitle = `Erro ao listar ${titulo}`;
        this.listOutput = this.formatError(err);
      }
    });
  }

  private setError(target: { kind: StatusKind; message: string }, err: unknown) {
    target.kind = 'error';
    target.message = this.formatError(err);
  }

  private formatError(err: unknown) {
    if (typeof err === 'string') return err;
    if (err && typeof err === 'object' && 'message' in err) {
      return String((err as { message?: string }).message);
    }
    return 'Erro inesperado';
  }

  private defaultDateTime() {
    const date = new Date();
    date.setDate(date.getDate() + 1);
    date.setHours(10, 0, 0, 0);
    return date.toISOString().slice(0, 16);
  }

  private toLocalDateTime(value: string) {
    if (!value) return value;
    return value.length === 16 ? `${value}:00` : value;
  }
}
