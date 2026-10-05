const API = 'http://localhost:8080';

const ROLES = [
    { key: 'la', label: 'LA', dbName: 'LA', emoji: '🎤' },
    { key: 'vocal1', label: 'Vocal 1', dbName: 'Vocal 1', emoji: '🎤' },
    { key: 'vocal2', label: 'Vocal 2', dbName: 'Vocal 2', emoji: '🎤' },
    { key: 'teclado', label: 'Teclado', dbName: 'Teclado', emoji: '🎹' },
    { key: 'violao', label: 'Violão', dbName: 'Violão', emoji: '🎸' },
    { key: 'guitarra', label: 'Guitarra', dbName: 'Guitarra', emoji: '🎸' },
    { key: 'baixo', label: 'Baixo', dbName: 'Baixo', emoji: '🎸' },
    { key: 'bateria', label: 'Bateria', dbName: 'Bateria', emoji: '🥁' }
];

let members = [];
let unavailability = [];
let schedules = [];
let currentAssignments = {};

const el = id => document.getElementById(id);

function formatDate(date) {
    if (!date) return '';
    const [ano, mes, dia] = date.split('-');
    return `${dia}/${mes}/${ano}`;
}

function getMember(id) {
    return members.find(member => member.id === Number(id));
}

function getMention(member) {
    if (!member) return '@';
    if (member.mencao) {
        return member.mencao.startsWith('@') ? member.mencao : '@' + member.mencao;
    }
    return '@' + member.nome.replace(/\s+/g, '');
}

function getUnavailable(memberId, date) {
    return unavailability.find(item =>
        item.membroId === Number(memberId) &&
        item.dataIndisponivel === date
    );
}

async function carregarTudo() {
    await Promise.all([
        carregarMembros(),
        carregarIndisponibilidades(),
        carregarEscalas()
    ]);

    renderTudo();
}

async function carregarMembros() {
    const response = await fetch(`${API}/membros`);
    if (!response.ok) throw new Error('Erro ao buscar membros');
    members = await response.json();
}

async function carregarIndisponibilidades() {
    const response = await fetch(`${API}/indisponibilidades`);
    if (!response.ok) throw new Error('Erro ao buscar indisponibilidades');
    unavailability = await response.json();
}

async function carregarEscalas() {
    const response = await fetch(`${API}/escalas`);
    if (!response.ok) throw new Error('Erro ao buscar escalas');
    schedules = await response.json();
}

function renderTudo() {
    renderMemberSelect();
    renderMembers();
    renderUnavailability();
    renderSavedSchedules();
    renderRoleSelectors();
}

function renderMemberSelect() {
    el('unavailableMember').innerHTML = `
        <option value="">Selecione...</option>
        ${members.map(member =>
        `<option value="${member.id}">${member.nome}</option>`
    ).join('')}
      `;
}

function renderMembers() {
    if (!members.length) {
        el('memberList').innerHTML = '<div class="empty">Nenhum membro encontrado no banco.</div>';
        return;
    }

    el('memberList').innerHTML = members.map(member => `
        <div class="item">
          <div>
            <strong>${member.nome}</strong>
            <div class="chips">
              ${(member.funcoes || []).map(funcao =>
        `<span class="chip">${funcao}</span>`
    ).join('')}
            </div>
            <div class="stats">
              🚫 ${member.quantidadeIndisponibilidades} indisponibilidade(s)
              &nbsp; • &nbsp;
              📅 ${member.quantidadeEscalas} escala(s)
            </div>
          </div>
        </div>
      `).join('');
}

function renderUnavailability() {
    if (!unavailability.length) {
        el('unavailabilityList').innerHTML = '<div class="empty">Nenhuma indisponibilidade cadastrada.</div>';
        return;
    }

    const lista = [...unavailability].sort((a, b) =>
        a.dataIndisponivel.localeCompare(b.dataIndisponivel)
    );

    el('unavailabilityList').innerHTML = lista.map(item => `
        <div class="item">
          <div>
            <strong>${item.membroNome}</strong>
            <div class="stats">
              ${formatDate(item.dataIndisponivel)}
              ${item.motivo ? ' • ' + item.motivo : ''}
            </div>
          </div>
          <button class="btn btn-danger" onclick="removerIndisponibilidade(${item.id})">Excluir</button>
        </div>
      `).join('');
}

function renderRoleSelectors() {
    const date = el('scheduleDate').value;

    el('roleSelectors').innerHTML = ROLES.map(role => {
        const candidatos = members.filter(member =>
            (member.funcoes || []).includes(role.dbName)
        );

        const options = candidatos.map(member => {
            const indisponivel = date && getUnavailable(member.id, date);
            const motivo = indisponivel?.motivo ? ` - ${indisponivel.motivo}` : '';

            return `
            <option value="${member.id}" ${indisponivel ? 'disabled' : ''}>
              ${member.nome}${indisponivel ? ' - INDISPONÍVEL' + motivo : ''}
            </option>
          `;
        }).join('');

        return `
          <div class="role-box">
            <label>${role.emoji} ${role.label}</label>
            <select data-role="${role.key}" ${!date ? 'disabled' : ''}>
              <option value="">${date ? 'Selecione...' : 'Escolha a data primeiro'}</option>
              ${options}
            </select>
            <div id="status-${role.key}" class="status-error"></div>
          </div>
        `;
    }).join('');

    document.querySelectorAll('[data-role]').forEach(select => {
        const roleKey = select.dataset.role;
        const selecionado = currentAssignments[roleKey];

        if (selecionado) {
            const option = [...select.options].find(opt =>
                Number(opt.value) === Number(selecionado)
            );

            if (option && !option.disabled) {
                select.value = selecionado;
            } else {
                currentAssignments[roleKey] = '';
            }
        }

        select.addEventListener('change', () => {
            const memberId = select.value;

            if (!memberId) {
                currentAssignments[roleKey] = '';
                return;
            }

            const indisponivel = getUnavailable(memberId, date);

            if (indisponivel) {
                select.value = '';
                currentAssignments[roleKey] = '';
                el(`status-${roleKey}`).textContent = 'Indisponível nessa data.';
                mostrarAviso(`${indisponivel.membroNome} está indisponível em ${formatDate(date)}.`);
                return;
            }

            el(`status-${roleKey}`).textContent = '';
            mostrarAviso('');
            currentAssignments[roleKey] = Number(memberId);
        });
    });
}

function mostrarAviso(texto) {
    const notice = el('availabilityNotice');
    notice.textContent = texto;
    notice.classList.toggle('show', Boolean(texto));
}

async function marcarIndisponibilidade() {
    const membroId = Number(el('unavailableMember').value);
    const dataIndisponivel = el('unavailableDate').value;
    const motivo = el('unavailableReason').value.trim();

    if (!membroId || !dataIndisponivel) {
        alert('Selecione o membro e a data.');
        return;
    }

    const response = await fetch(`${API}/indisponibilidades`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            membroId,
            dataIndisponivel,
            motivo
        })
    });

    if (!response.ok) {
        alert(await response.text());
        return;
    }

    el('unavailableDate').value = '';
    el('unavailableReason').value = '';

    await carregarTudo();
}

async function removerIndisponibilidade(id) {
    const response = await fetch(`${API}/indisponibilidades/${id}`, {
        method: 'DELETE'
    });

    if (!response.ok) {
        alert('Não foi possível excluir a indisponibilidade.');
        return;
    }

    await carregarTudo();
}

function montarItensEscala() {
    return ROLES
        .filter(role => currentAssignments[role.key])
        .map(role => ({
            membroId: Number(currentAssignments[role.key]),
            funcao: role.dbName
        }));
}

async function gerarEscala() {
    const dataEscala = el('scheduleDate').value;

    if (!dataEscala) {
        alert('Escolha a data da escala.');
        return;
    }

    for (const role of ROLES) {
        const memberId = currentAssignments[role.key];
        if (memberId && getUnavailable(memberId, dataEscala)) {
            alert('Existe uma pessoa indisponível nessa escala.');
            return;
        }
    }

    const response = await fetch(`${API}/escalas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            dataEscala,
            itens: montarItensEscala()
        })
    });

    if (!response.ok) {
        alert(await response.text());
        return;
    }

    el('scaleOutput').textContent = buildScaleText();
    await carregarTudo();
    alert('Escala salva com sucesso.');
}

function buildScaleText() {
    function pessoa(roleKey) {
        return getMention(getMember(currentAssignments[roleKey]));
    }

    return `🎤 VOCAL
* LA: ${pessoa('la')}
* Vocal 1: ${pessoa('vocal1')}
* Vocal 2: ${pessoa('vocal2')}

🎸 INSTRUMENTAL
* Teclado: ${pessoa('teclado')}
* Violão : ${pessoa('violao')}
* Guitarra: ${pessoa('guitarra')}
* Baixo: ${pessoa('baixo')}
* Bateria: ${pessoa('bateria')}`;
}

function renderSavedSchedules() {
    el('savedScheduleSelect').innerHTML = `
        <option value="">Selecione...</option>
        ${schedules.map(schedule =>
        `<option value="${schedule.dataEscala}">${formatDate(schedule.dataEscala)}</option>`
    ).join('')}
      `;

    if (!schedules.length) {
        el('savedList').innerHTML = '<div class="empty">Nenhuma escala salva.</div>';
        return;
    }

    el('savedList').innerHTML = schedules.map(schedule => `
        <div class="item">
          <div>
            <strong>${formatDate(schedule.dataEscala)}</strong>
            <div class="stats">${schedule.itens?.length || 0} função(ões) preenchida(s)</div>
          </div>
          <div class="actions" style="margin-top:0;">
            <button class="btn btn-soft" onclick="abrirEscala('${schedule.dataEscala}')">Abrir</button>
            <button class="btn btn-danger" onclick="excluirEscala('${schedule.dataEscala}')">Excluir</button>
          </div>
        </div>
      `).join('');
}

async function abrirEscala(data) {
    const response = await fetch(`${API}/escalas/${data}`);

    if (!response.ok) {
        alert('Escala não encontrada.');
        return;
    }

    const escala = await response.json();
    currentAssignments = {};

    for (const item of escala.itens || []) {
        const role = ROLES.find(role => role.dbName === item.funcao);
        if (role) currentAssignments[role.key] = item.membroId;
    }

    el('scheduleDate').value = data;
    renderRoleSelectors();
    el('scaleOutput').textContent = buildScaleText();
    el('savedScheduleSelect').value = data;
}

async function excluirEscala(data) {
    const response = await fetch(`${API}/escalas/${data}`, {
        method: 'DELETE'
    });

    if (!response.ok) {
        alert('Não foi possível excluir a escala.');
        return;
    }

    currentAssignments = {};
    el('scaleOutput').textContent = 'Escolha uma data e monte a escala.';
    await carregarTudo();
}

async function copiarTexto() {
    await navigator.clipboard.writeText(el('scaleOutput').textContent);
    alert('Texto copiado.');
}

el('scheduleDate').addEventListener('change', async () => {
    const data = el('scheduleDate').value;
    currentAssignments = {};
    mostrarAviso('');

    const escalaExistente = schedules.find(item => item.dataEscala === data);
    if (escalaExistente) {
        await abrirEscala(data);
    } else {
        renderRoleSelectors();
        el('scaleOutput').textContent = 'Escolha os membros e clique em “Gerar escala”.';
    }
});

el('savedScheduleSelect').addEventListener('change', event => {
    if (event.target.value) abrirEscala(event.target.value);
});

el('addUnavailableBtn').addEventListener('click', marcarIndisponibilidade);
el('generateBtn').addEventListener('click', gerarEscala);
el('copyBtn').addEventListener('click', copiarTexto);
el('clearScaleBtn').addEventListener('click', () => {
    currentAssignments = {};
    renderRoleSelectors();
    el('scaleOutput').textContent = 'Seleção limpa.';
});

carregarTudo().catch(error => {
    console.error(error);
    alert('Não foi possível conectar com a API. Confira se o Spring Boot está rodando na porta 8080.');
});
