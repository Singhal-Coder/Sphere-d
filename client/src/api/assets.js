import api from './axios.js'

export async function getAssets(page = 0, size = 20) {
  const { data } = await api.get('/assets', { params: { page, size } })
  return data
}

export async function getAssetById(serialNumber) {
  const { data } = await api.get(`/assets/${encodeURIComponent(serialNumber)}`)
  return data
}

export async function createAsset(dto) {
  const { data } = await api.post('/assets', dto)
  return data
}

export async function updateAsset(serialNumber, dto) {
  const { data } = await api.patch(`/assets/${encodeURIComponent(serialNumber)}`, dto)
  return data
}

export async function changeAssetStatus(serialNumber, status) {
  const { data } = await api.patch(
    `/assets/${encodeURIComponent(serialNumber)}/status`,
    null,
    { params: { status } },
  )
  return data
}

export async function deleteAsset(serialNumber) {
  const { data } = await api.delete(`/assets/${encodeURIComponent(serialNumber)}`)
  return data
}
