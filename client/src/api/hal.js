/** Extract Spring HATEOAS link rel href from ApiResponse */
export function getLinkHref(apiResponse, rel) {
  const links = apiResponse?._links
  if (!links || !links[rel]) return null
  const entry = links[rel]
  return typeof entry === 'string' ? entry : entry?.href ?? null
}

export function getPageContent(apiResponse) {
  const page = apiResponse?.data
  if (!page) return []
  return page.content ?? []
}
